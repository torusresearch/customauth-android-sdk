package org.torusresearch.torusdirect.handlers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.browser.customtabs.CustomTabsIntent;

import com.google.gson.Gson;

import org.torusresearch.torusdirect.types.State;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class GoogleHandler implements ILoginHandler {
    private static String SCOPE = "profile email openid";
    private static String RESPONSE_TYPE = "token id_token";
    private static String PROMPT = "consent select_account";
    private final String instanceId;
    private final String clientId;
    private final String redirectUri;
    private final String verifier;

    public GoogleHandler(String _instanceId, String _clientId, String _redirectUri, String _verifier) {
        instanceId = _instanceId;
        clientId = _clientId;
        redirectUri = _redirectUri;
        verifier = _verifier;
    }

    public String getPrompt() {
        return PROMPT;
    }

    @Override
    public String getScope() {
        return SCOPE;
    }

    @Override
    public String getResponseType() {
        return RESPONSE_TYPE;
    }

    @Override
    public String getState() {
        State localState = new State(this.instanceId, this.verifier);
        Gson gson = new Gson();
        return gson.toJson(localState);
    }

    @Override
    public String getFinalUrl() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("https://accounts.google.com/o/oauth2/v2/auth?response_type=");
            sb.append(URLEncoder.encode(this.getResponseType(), "utf-8"));
            sb.append("&client_id=");
            sb.append(this.clientId);
            sb.append("&state=");
            sb.append(URLEncoder.encode(this.getState(), "utf-8"));
            sb.append("&scope=");
            sb.append(URLEncoder.encode(this.getScope(), "utf-8"));
            sb.append("&redirect_uri=");
            sb.append(URLEncoder.encode(redirectUri, "utf-8"));
            sb.append("&nonce=");
            sb.append(this.instanceId);
            sb.append("&prompt=");
            sb.append(URLEncoder.encode(this.getPrompt(), "utf-8"));
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public Intent handleLogin(Context ctx) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent intent = builder.build();
        intent.intent.setData(Uri.parse(this.getFinalUrl()));
        return intent.intent;
    }

    public UserInfo
}
