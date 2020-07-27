package org.torusresearch.torusdirect.interfaces;

import org.torusresearch.torusdirect.types.LoginWindowResponse;
import org.torusresearch.torusdirect.types.TorusVerifierResponse;

import java.util.concurrent.CompletableFuture;

public interface ILoginHandler {
    CompletableFuture<TorusVerifierResponse> getUserInfo(LoginWindowResponse params);

    CompletableFuture<LoginWindowResponse> handleLoginWindow();
}
