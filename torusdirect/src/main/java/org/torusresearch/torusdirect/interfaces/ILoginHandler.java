package org.torusresearch.torusdirect.interfaces;

import android.content.Context;

import org.torusresearch.torusdirect.types.LoginWindowResponse;
import org.torusresearch.torusdirect.types.TorusVerifierResponse;

import java8.util.concurrent.CompletableFuture;

public interface ILoginHandler {
    CompletableFuture<TorusVerifierResponse> getUserInfo(LoginWindowResponse params);

    CompletableFuture<LoginWindowResponse> handleLoginWindow(Context context);

    void setResponse(String response);

    String getFinalURL();
}
