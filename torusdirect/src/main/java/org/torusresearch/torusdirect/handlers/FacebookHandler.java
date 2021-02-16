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

final class FacebookUserInfoResult {
    private final String id;
    private final String email;
    private final String name;
    private final Picture picture;

    public FacebookUserInfoResult(Picture picture, String email, String name, String id) {
        this.picture = picture;
        this.email = email;
        this.name = name;
        this.id = id;
    }

    public Picture getPicture() {
        return picture;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public static class Picture {
        private final Data data;

        public Picture(Data data) {
            this.data = data;
        }

        public Data getData() {
            return data;
        }

        public static class Data {
            private final String url;

            public Data(String url) {
                this.url = url;
            }

            public String getUrl() {
                return url;
            }
        }
    }
}


public class FacebookHandler extends AbstractLoginHandler {

    private final String RESPONSE_TYPE = "token";

    private final String SCOPE = "public_profile email";

    public FacebookHandler(CreateHandlerParams _params) {
        super(_params);
        this.setFinalUrl();
    }

    @Override
    protected void setFinalUrl() {
        HttpUrl.Builder finalUrl = new HttpUrl.Builder().scheme("https").host("www.facebook.com").addPathSegments("v6.0/dialog/oauth");
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
        return HttpHelpers.get("https://graph.facebook.com/me?fields=name,email,picture.type(large)", new Header[]{
                new Header("Authorization", "Bearer " + accessToken)
        }).thenComposeAsync(resp -> {
            Gson gson = new Gson();
            FacebookUserInfoResult result = gson.fromJson(resp, FacebookUserInfoResult.class);
            return CompletableFuture.supplyAsync(() -> new TorusVerifierResponse(result.getEmail(), result.getName(),
                    result.getPicture().getData().getUrl(), this.params.getVerifier(), result.getId().toLowerCase(), this.params.getTypeOfLogin()));
        });

    }
}
