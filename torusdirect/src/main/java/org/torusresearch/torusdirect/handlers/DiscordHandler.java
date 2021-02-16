package org.torusresearch.torusdirect.handlers;

import android.util.Log;

import com.google.gson.Gson;

import org.torusresearch.torusdirect.types.CreateHandlerParams;
import org.torusresearch.torusdirect.types.LoginWindowResponse;
import org.torusresearch.torusdirect.types.TorusVerifierResponse;
import org.torusresearch.torusdirect.utils.Helpers;
import org.torusresearch.torusdirect.utils.HttpHelpers;

import java8.util.concurrent.CompletableFuture;
import okhttp3.HttpUrl;
import okhttp3.internal.http2.Header;

final class DiscordUserInfoResult {
    private final String id;
    private final String username;
    private final String discriminator;
    private final String avatar;
    private final String email;

    public DiscordUserInfoResult(String id, String username, String discriminator, String avatar, String email) {
        this.id = id;
        this.username = username;
        this.discriminator = discriminator;
        this.avatar = avatar;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getEmail() {
        return email;
    }

}


public class DiscordHandler extends AbstractLoginHandler {

    private final String RESPONSE_TYPE = "token";

    private final String SCOPE = "identify email";

    public DiscordHandler(CreateHandlerParams _params) {
        super(_params);
        this.setFinalUrl();
    }

    @Override
    protected void setFinalUrl() {
        HttpUrl.Builder finalUrl = new HttpUrl.Builder().scheme("https").host("discordapp.com").addPathSegments("api/oauth2/authorize");
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
        return HttpHelpers.get("https://discordapp.com/api/users/@me", new Header[]{
                new Header("Authorization", "Bearer " + accessToken)
        }).thenComposeAsync(resp -> {
            Gson gson = new Gson();
            DiscordUserInfoResult result = gson.fromJson(resp, DiscordUserInfoResult.class);
            String profileImage =
                    Helpers.isEmpty(result.getAvatar())
                            ? "https://cdn.discordapp.com/embed/avatars/" + Integer.parseInt(result.getDiscriminator()) % 5 + ".png"
                            : "https://cdn.discordapp.com/avatars/" + result.getId() + "/" + result.getAvatar() + ".png?size=2048";
            return CompletableFuture.supplyAsync(() -> new TorusVerifierResponse(result.getEmail(), result.getUsername() + "#" + result.getDiscriminator(),
                    profileImage, this.params.getVerifier(), result.getId(), this.params.getTypeOfLogin()));
        });

    }
}
