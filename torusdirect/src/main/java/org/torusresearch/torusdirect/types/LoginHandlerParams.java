package org.torusresearch.torusdirect.types;

public class LoginHandlerParams {
    private final String clientId;
    private final String verifier;
    private final String redirect_uri;
    private final LoginType typeOfLogin;
    private final String browserRedirectUri;

    public LoginHandlerParams(String clientId, String verifier, String redirect_uri, LoginType typeOfLogin, String browserRedirectUri) {
        this.clientId = clientId;
        this.verifier = verifier;
        this.redirect_uri = redirect_uri;
        this.typeOfLogin = typeOfLogin;
        this.browserRedirectUri = browserRedirectUri;
    }

    public String getBrowserRedirectUri() {
        return browserRedirectUri;
    }

    public String getClientId() {
        return clientId;
    }

    public String getVerifier() {
        return verifier;
    }

    public String getRedirect_uri() {
        return redirect_uri;
    }

    public LoginType getTypeOfLogin() {
        return typeOfLogin;
    }
}
