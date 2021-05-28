package org.torusresearch.torusdirect.interfaces;

import android.content.Context;

import org.torusresearch.torusdirect.types.LoginWindowResponse;
import org.torusresearch.torusdirect.types.TorusVerifierResponse;

import java8.util.concurrent.CompletableFuture;

public interface ILoginHandler {
    CompletableFuture<TorusVerifierResponse> getUserInfo(LoginWindowResponse params);

    CompletableFuture<LoginWindowResponse> handleLoginWindow(Context context, boolean isNewActivity, boolean preferCustomTabs, String[] allowedBrowsers);

    void setResponse(String response);

    void setResponse(Exception exception);

    String getFinalURL();
}
