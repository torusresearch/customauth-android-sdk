package org.torusresearch.customauth.interfaces;

import android.content.Context;

import org.torusresearch.customauth.types.LoginWindowResponse;
import org.torusresearch.customauth.types.TorusVerifierResponse;

import java8.util.concurrent.CompletableFuture;

public interface ILoginHandler {
    CompletableFuture<TorusVerifierResponse> getUserInfo(LoginWindowResponse params);

    CompletableFuture<LoginWindowResponse> handleLoginWindow(Context context, boolean isNewActivity, boolean preferCustomTabs, String[] allowedBrowsers);

    void setResponse(String response);

    void setResponse(Exception exception);

    String getFinalURL();
}
