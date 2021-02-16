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

final class RedditUserInfoResult {
    private final String icon_img;
    private final String name;

    public RedditUserInfoResult(String icon_img, String name) {
        this.icon_img = icon_img;
        this.name = name;
    }

    public String getIcon_img() {
        return icon_img;
    }

    public String getName() {
        return name;
    }
}


public class RedditHandler extends AbstractLoginHandler {

    private final String RESPONSE_TYPE = "token";

    private final String SCOPE = "identity";

    public RedditHandler(CreateHandlerParams _params) {
        super(_params);
        this.setFinalUrl();
    }

    @Override
    protected void setFinalUrl() {
        HttpUrl.Builder finalUrl = new HttpUrl.Builder().scheme("https").host("www.reddit.com").addPathSegments("api/v1/authorize.compact");
        finalUrl.addQueryParameter("response_type", this.RESPONSE_TYPE);
        finalUrl.addQueryParameter("client_id", this.params.getClientId());
        finalUrl.addQueryParameter("state", this.getState());
        finalUrl.addQueryParameter("scope", this.SCOPE);
        finalUrl.addQueryParameter("redirect_uri", this.params.getBrowserRedirectUri());
        this.finalURL = finalUrl.build().toString();
        Log.d("finalUrl:torus", this.finalURL);
    }

    @Override
    public CompletableFuture<TorusVerifierResponse> getUserInfo(LoginWindowResponse params) {
        String accessToken = params.getAccessToken();
        return HttpHelpers.get("https://oauth.reddit.com/api/v1/me", new Header[]{
                new Header("Authorization", "Bearer " + accessToken)
        }).thenComposeAsync(resp -> {
            Gson gson = new Gson();
            RedditUserInfoResult result = gson.fromJson(resp, RedditUserInfoResult.class);
            return CompletableFuture.supplyAsync(() -> new TorusVerifierResponse("", result.getName(), result.getIcon_img().split("\\?").length > 0 ?
                    result.getIcon_img().split("\\?")[0] : result.getIcon_img(), this.params.getVerifier(), result.getName().toLowerCase(), this.params.getTypeOfLogin()));
        });

    }
}
