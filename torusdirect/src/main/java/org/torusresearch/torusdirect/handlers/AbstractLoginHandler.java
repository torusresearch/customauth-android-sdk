package org.torusresearch.torusdirect.handlers;

import com.google.gson.Gson;

import org.torusresearch.torusdirect.interfaces.ILoginHandler;
import org.torusresearch.torusdirect.types.CreateHandlerParams;
import org.torusresearch.torusdirect.types.LoginWindowResponse;
import org.torusresearch.torusdirect.types.State;
import org.torusresearch.torusdirect.types.TorusVerifierResponse;

import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractLoginHandler implements ILoginHandler {
    protected final String nonce = UUID.randomUUID().toString();
    protected CreateHandlerParams params;
    protected String finalURL;

    public AbstractLoginHandler(CreateHandlerParams _params) {
        params = _params;
    }

    public String getState() {
        State localState = new State(this.nonce, this.params.getVerifier());
        Gson gson = new Gson();
        String stringifiedState = gson.toJson(localState, State.class);
        return Base64.getUrlEncoder().encodeToString(Base64.getEncoder().encode(stringifiedState.getBytes()));
    }

    protected abstract void setFinalUrl();

    @Override
    public abstract CompletableFuture<TorusVerifierResponse> getUserInfo(LoginWindowResponse params);

    @Override
    public CompletableFuture<LoginWindowResponse> handleLoginWindow() {
        throw new UnsupportedOperationException("Implement this method");
    }
}
