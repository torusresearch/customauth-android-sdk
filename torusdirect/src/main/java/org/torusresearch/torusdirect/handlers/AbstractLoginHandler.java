package org.torusresearch.torusdirect.handlers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import org.torusresearch.torusdirect.activity.StartUpActivity;
import org.torusresearch.torusdirect.interfaces.ILoginHandler;
import org.torusresearch.torusdirect.types.CreateHandlerParams;
import org.torusresearch.torusdirect.types.LoginWindowResponse;
import org.torusresearch.torusdirect.types.State;
import org.torusresearch.torusdirect.types.TorusVerifierResponse;

import java.util.Base64;
import java.util.UUID;

import java8.util.concurrent.CompletableFuture;

public abstract class AbstractLoginHandler implements ILoginHandler {
    protected final String nonce = UUID.randomUUID().toString();
    protected CreateHandlerParams params;
    protected String finalURL;
    private CompletableFuture<LoginWindowResponse> loginWindowResponseCompletableFuture;

    public AbstractLoginHandler(CreateHandlerParams _params) {
        params = _params;
        loginWindowResponseCompletableFuture = new CompletableFuture<>();
    }

    public String getState() {
        State localState = new State(this.nonce, this.params.getVerifier(), this.params.getRedirect_uri());
        Gson gson = new Gson();
        String stringifiedState = gson.toJson(localState, State.class);
        return Base64.getUrlEncoder().encodeToString(stringifiedState.getBytes());
    }

    @Override
    public void setResponse(String response) {
        LoginWindowResponse loginWindowResponse = new LoginWindowResponse();
        loginWindowResponse.parseResponse(response);
        Log.d(AbstractLoginHandler.class.getSimpleName(), loginWindowResponse.toString());
        loginWindowResponseCompletableFuture.complete(loginWindowResponse);
    }

    protected abstract void setFinalUrl();

    @Override
    public abstract CompletableFuture<TorusVerifierResponse> getUserInfo(LoginWindowResponse params);

    @Override
    public CompletableFuture<LoginWindowResponse> handleLoginWindow(Context context) {
        if (StartUpActivity.loginHandler != null && StartUpActivity.loginHandler.get() == null) {
            StartUpActivity.loginHandler.set(this);
        }
        context.startActivity(new Intent(context, StartUpActivity.class).putExtra(StartUpActivity.URL, finalURL));
        return loginWindowResponseCompletableFuture;
    }
}
