package org.torusresearch.torusdirect.handlers;

import android.util.Log;

import com.google.gson.Gson;

import org.torusresearch.torusdirect.types.CreateHandlerParams;
import org.torusresearch.torusdirect.types.LoginWindowResponse;
import org.torusresearch.torusdirect.types.TorusVerifierResponse;
import org.torusresearch.torusdirect.utils.HttpHelpers;

import java8.util.concurrent.CompletableFuture;
import okhttp3.HttpUrl;
import okhttp3.internal.http2.Header;

final class GoogleUserInfoResult {
    private final String picture;
    private final String email;
    private final String name;

    public GoogleUserInfoResult(String picture, String email, String name) {
        this.picture = picture;
        this.email = email;
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}


public class GoogleHandler extends AbstractLoginHandler {

    private final String RESPONSE_TYPE = "token id_token";

    private final String SCOPE = "profile email openid";

    private final String PROMPT = "consent select_account";

    public GoogleHandler(CreateHandlerParams _params) {
        super(_params);
        this.setFinalUrl();
    }

    @Override
    protected void setFinalUrl() {
        HttpUrl.Builder finalUrl = new HttpUrl.Builder().scheme("https").host("accounts.google.com").addPathSegments("o/oauth2/v2/auth");
        finalUrl.addQueryParameter("response_type", this.RESPONSE_TYPE);
        finalUrl.addQueryParameter("client_id", this.params.getClientId());
        finalUrl.addQueryParameter("state", this.getState());
        finalUrl.addQueryParameter("scope", this.SCOPE);
        finalUrl.addQueryParameter("redirect_uri", this.params.getBrowserRedirectUri());
        finalUrl.addQueryParameter("nonce", this.nonce);
        finalUrl.addQueryParameter("prompt", this.PROMPT);
        this.finalURL = finalUrl.build().toString();
        Log.d("finalUrl:torus", this.finalURL);
    }

    @Override
    public CompletableFuture<TorusVerifierResponse> getUserInfo(LoginWindowResponse params) {
        String accessToken = params.getAccessToken();
        return HttpHelpers.get("https://www.googleapis.com/userinfo/v2/me", new Header[]{
                new Header("Authorization", "Bearer " + accessToken)
        }).thenComposeAsync(resp -> {
            Gson gson = new Gson();
            GoogleUserInfoResult result = gson.fromJson(resp, GoogleUserInfoResult.class);
            return CompletableFuture.supplyAsync(() -> new TorusVerifierResponse(result.getEmail(), result.getName(), result.getPicture(), this.params.getVerifier(), result.getEmail().toLowerCase(), this.params.getTypeOfLogin()));
        });

    }
}
