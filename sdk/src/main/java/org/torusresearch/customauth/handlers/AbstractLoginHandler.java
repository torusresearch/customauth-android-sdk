package org.torusresearch.customauth.handlers;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import org.torusresearch.customauth.activity.StartUpActivity;
import org.torusresearch.customauth.interfaces.ILoginHandler;
import org.torusresearch.customauth.types.CreateHandlerParams;
import org.torusresearch.customauth.types.LoginWindowResponse;
import org.torusresearch.customauth.types.State;
import org.torusresearch.customauth.types.TorusVerifierResponse;
import org.torusresearch.customauth.types.UserCancelledException;

import java.util.UUID;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractLoginHandler implements ILoginHandler {
    protected final String nonce = UUID.randomUUID().toString();
    private final CompletableFuture<LoginWindowResponse> loginWindowResponseCompletableFuture;
    protected CreateHandlerParams params;
    protected String finalURL;

    public AbstractLoginHandler(CreateHandlerParams _params) {
        params = _params;
        loginWindowResponseCompletableFuture = new CompletableFuture<>();
    }

    public String getState() {
        State localState = new State(this.nonce, this.params.getVerifier(), this.params.getRedirect_uri());
        Gson gson = new Gson();
        String stringifiedState = gson.toJson(localState, State.class);
        return Base64.encodeToString(stringifiedState.getBytes(), Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
    }

    @Override
    public void setResponse(String response) {
        if (response != null) {
            LoginWindowResponse loginWindowResponse = new LoginWindowResponse();
            loginWindowResponse.parseResponse(response);
            Log.d(AbstractLoginHandler.class.getSimpleName(), loginWindowResponse.toString());
            loginWindowResponseCompletableFuture.complete(loginWindowResponse);
        } else {
            loginWindowResponseCompletableFuture.completeExceptionally(new UserCancelledException());
        }
    }

    @Override
    public void setResponse(Exception exception) {
        loginWindowResponseCompletableFuture.completeExceptionally(exception);
    }

    public String getFinalURL() {
        return this.finalURL;
    }

    protected abstract void setFinalUrl();

    @Override
    public abstract CompletableFuture<TorusVerifierResponse> getUserInfo(LoginWindowResponse params);

    @Override
    public CompletableFuture<LoginWindowResponse> handleLoginWindow(Context context, boolean isNewActivity, boolean preferCustomTabs, String[] allowedBrowsers) {
        if (StartUpActivity.loginHandler != null && StartUpActivity.loginHandler.get() == null) {
            StartUpActivity.loginHandler.set(this);
        }
        Intent startupIntent = new Intent(context, StartUpActivity.class)
                .putExtra(StartUpActivity.URL, finalURL)
                .putExtra(StartUpActivity.PREFER_CUSTOM_TABS, preferCustomTabs)
                .putExtra(StartUpActivity.ALLOWED_BROWSERS, allowedBrowsers);
        if (isNewActivity) {
            startupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        context.startActivity(startupIntent);
        return loginWindowResponseCompletableFuture;
    }
}
