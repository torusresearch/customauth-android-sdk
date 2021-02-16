package org.torusresearch.torusdirect.handlers;

import android.util.Log;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;

import org.torusresearch.torusdirect.types.Auth0ClientOptions;
import org.torusresearch.torusdirect.types.CreateHandlerParams;
import org.torusresearch.torusdirect.types.Display;
import org.torusresearch.torusdirect.types.JwtUserInfoResult;
import org.torusresearch.torusdirect.types.LoginType;
import org.torusresearch.torusdirect.types.LoginWindowResponse;
import org.torusresearch.torusdirect.types.Prompt;
import org.torusresearch.torusdirect.types.TorusVerifierResponse;
import org.torusresearch.torusdirect.utils.Helpers;
import org.torusresearch.torusdirect.utils.HttpHelpers;

import java.util.HashMap;
import java.util.Map;

import java8.util.concurrent.CompletableFuture;
import okhttp3.HttpUrl;
import okhttp3.internal.http2.Header;


public class JwtHandler extends AbstractLoginHandler {

    private final String RESPONSE_TYPE = "token id_token";

    private final String SCOPE = "profile email openid";

    private final Prompt PROMPT = Prompt.LOGIN;

    public JwtHandler(CreateHandlerParams _params) {
        super(_params);
        this.setFinalUrl();
    }

    @Override
    protected void setFinalUrl() {
        String domain = this.params.getJwtParams().getDomain();
        HttpUrl.Builder finalUrl = new HttpUrl.Builder().scheme("https").host(domain).addPathSegments("authorize");
        Auth0ClientOptions localOptions = new Auth0ClientOptions.Auth0ClientOptionsBuilder(domain)
                .setClient_id(this.params.getClientId())
                .setPrompt(this.PROMPT)
                .setScope(this.SCOPE)
                .setConnection(Helpers.loginToConnectionMap.get(this.params.getTypeOfLogin())).build();
        // These are separate because they must not be set by user using builder
        localOptions.setResponse_type(this.RESPONSE_TYPE);
        localOptions.setState(this.getState());
        localOptions.setNonce(this.nonce);

        Auth0ClientOptions userOptions = this.params.getJwtParams();
        Auth0ClientOptions finalOptions = localOptions.merge(userOptions);

        String finalOptionsDomain = finalOptions.getDomain();
        String finalOptionsClientId = finalOptions.getClient_id();
        String finalOptionsLeeway = finalOptions.getLeeway();
        Display finalOptionsDisplay = finalOptions.getDisplay();
        Prompt finalOptionsPrompt = finalOptions.getPrompt();
        String finalOptionsMaxAge = finalOptions.getMax_age();
        String finalOptionsUiLocales = finalOptions.getUi_locales();
        String finalOptionsIdTokenHint = finalOptions.getId_token_hint();
        String finalOptionsLoginHint = finalOptions.getLogin_hint();
        String finalOptionsAcrValues = finalOptions.getAcr_values();
        String finalOptionsScope = finalOptions.getScope();
        String finalOptionsAudience = finalOptions.getAudience();
        String finalOptionsConnection = finalOptions.getConnection();
        HashMap<String, String> finalOptionsAdditionalParams = finalOptions.getAdditionalParams();
        String finalOptionsState = finalOptions.getState();
        String finalOptionsResponseType = finalOptions.getResponse_type();
        String finalOptionsNonce = finalOptions.getNonce();

        // mandatory
        finalUrl.addQueryParameter("domain", finalOptionsDomain);
        finalUrl.addQueryParameter("state", finalOptionsState);
        finalUrl.addQueryParameter("response_type", finalOptionsResponseType);
        finalUrl.addQueryParameter("nonce", finalOptionsNonce);
        finalUrl.addQueryParameter("client_id", finalOptionsClientId);
        finalUrl.addQueryParameter("redirect_uri", this.params.getBrowserRedirectUri());
        // optional
        if (Helpers.isValid(finalOptionsLeeway))
            finalUrl.addQueryParameter("leeway", finalOptionsLeeway);
        if (Helpers.isValid(finalOptionsDisplay))
            finalUrl.addQueryParameter("display", finalOptionsDisplay.toString());
        if (Helpers.isValid(finalOptionsPrompt))
            finalUrl.addQueryParameter("prompt", finalOptionsPrompt.toString());
        if (Helpers.isValid(finalOptionsMaxAge))
            finalUrl.addQueryParameter("max_age", finalOptionsMaxAge);
        if (Helpers.isValid(finalOptionsUiLocales))
            finalUrl.addQueryParameter("ui_locales", finalOptionsUiLocales);
        if (Helpers.isValid(finalOptionsIdTokenHint))
            finalUrl.addQueryParameter("id_token_hint", finalOptionsIdTokenHint);
        if (Helpers.isValid(finalOptionsLoginHint))
            finalUrl.addQueryParameter("login_hint", finalOptionsLoginHint);
        if (Helpers.isValid(finalOptionsAcrValues))
            finalUrl.addQueryParameter("acr_values", finalOptionsAcrValues);
        if (Helpers.isValid(finalOptionsScope))
            finalUrl.addQueryParameter("scope", finalOptionsScope);
        if (Helpers.isValid(finalOptionsAudience))
            finalUrl.addQueryParameter("audience", finalOptionsAudience);
        if (Helpers.isValid(finalOptionsConnection))
            finalUrl.addQueryParameter("connection", finalOptionsConnection);
        for (Map.Entry<String, String> entry : finalOptionsAdditionalParams.entrySet()) {
            finalUrl.addQueryParameter(entry.getKey(), entry.getValue());
        }
        this.finalURL = finalUrl.build().toString();
        Log.d("finalUrl:torus", this.finalURL);
    }

    @Override
    public CompletableFuture<TorusVerifierResponse> getUserInfo(LoginWindowResponse params) {
        String accessToken = params.getAccessToken();
        String idToken = params.getIdToken();
        String domain = this.params.getJwtParams().getDomain();
        String verifierIdField = this.params.getJwtParams().getVerifierIdField();
        boolean isVerifierIdCaseSensitive = this.params.getJwtParams().getVerifierIdCaseSensitive();
        LoginType typeOfLogin = this.params.getTypeOfLogin();
        HttpUrl.Builder userInfoUrl = new HttpUrl.Builder().scheme("https").host(domain).addPathSegments("userinfo");
        return HttpHelpers.get(userInfoUrl.toString(), new Header[]{
                new Header("Authorization", "Bearer " + accessToken)
        }).thenComposeAsync(resp -> {
            Gson gson = new Gson();
            JwtUserInfoResult result = gson.fromJson(resp, JwtUserInfoResult.class);
            return CompletableFuture.supplyAsync(() -> new TorusVerifierResponse(result.getEmail(), result.getName(),
                    result.getPicture(), this.params.getVerifier(), Helpers.getVerifierId(result, typeOfLogin, verifierIdField, isVerifierIdCaseSensitive), typeOfLogin));
        }).handleAsync((res, err) -> {
            if (res != null) return res;
            DecodedJWT decodedJWT = JWT.decode(idToken);
            String name = decodedJWT.getClaim("name").asString();
            String email = decodedJWT.getClaim("email").asString();
            String picture = decodedJWT.getClaim("picture").asString();
            String sub = decodedJWT.getClaim("sub").asString();
            String nickname = decodedJWT.getClaim("nickname").asString();
            JwtUserInfoResult result = new JwtUserInfoResult(picture, email, name, sub, nickname);
            return new TorusVerifierResponse(email, name, picture, this.params.getVerifier(), Helpers.getVerifierId(result, typeOfLogin, verifierIdField, isVerifierIdCaseSensitive), typeOfLogin);
        });
    }
}
