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

final class TwitchUserInfoResult {
    private final Data[] data;

    public TwitchUserInfoResult(Data[] data) {
        this.data = data;
    }

    public Data[] getData() {
        return data;
    }

    static class Data {
        private final String profile_image_url;
        private final String display_name;
        private final String email;
        private final String id;

        public Data(String profile_image_url, String display_name, String email, String id) {
            this.profile_image_url = profile_image_url;
            this.display_name = display_name;
            this.email = email;
            this.id = id;
        }

        public String getProfile_image_url() {
            return profile_image_url;
        }

        public String getDisplay_name() {
            return display_name;
        }

        public String getEmail() {
            return email;
        }

        public String getId() {
            return id;
        }
    }
}


public class TwitchHandler extends AbstractLoginHandler {

    private final String RESPONSE_TYPE = "token";

    private final String SCOPE = "user:read:email";

    public TwitchHandler(CreateHandlerParams _params) {
        super(_params);
        this.setFinalUrl();
    }

    @Override
    protected void setFinalUrl() {
        HttpUrl.Builder finalUrl = new HttpUrl.Builder().scheme("https").host("id.twitch.tv").addPathSegments("oauth2/authorize");
        finalUrl.addQueryParameter("response_type", this.RESPONSE_TYPE);
        finalUrl.addQueryParameter("client_id", this.params.getClientId());
        finalUrl.addQueryParameter("state", this.getState());
        finalUrl.addQueryParameter("scope", this.SCOPE);
        finalUrl.addQueryParameter("redirect_uri", this.params.getBrowserRedirectUri());
        finalUrl.addQueryParameter("force_verify", "true");
        this.finalURL = finalUrl.build().toString();
        Log.d("finalUrl:torus", this.finalURL);
    }

    @Override
    public CompletableFuture<TorusVerifierResponse> getUserInfo(LoginWindowResponse params) {
        String accessToken = params.getAccessToken();
        return HttpHelpers.get("https://api.twitch.tv/helix/users", new Header[]{
                new Header("Authorization", "Bearer " + accessToken),
                new Header("Client-ID", this.params.getClientId())
        }).thenComposeAsync(resp -> {
            Gson gson = new Gson();
            TwitchUserInfoResult dataResult = gson.fromJson(resp, TwitchUserInfoResult.class);
            TwitchUserInfoResult.Data result = dataResult.getData()[0];
            return CompletableFuture.supplyAsync(() -> new TorusVerifierResponse(result.getEmail(), result.getDisplay_name(), result.getProfile_image_url(),
                    this.params.getVerifier(), result.getId().toLowerCase(), this.params.getTypeOfLogin()));
        });

    }
}
