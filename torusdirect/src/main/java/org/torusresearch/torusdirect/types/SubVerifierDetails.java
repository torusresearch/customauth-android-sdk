package org.torusresearch.torusdirect.types;

public class SubVerifierDetails {
    private LoginType typeOfLogin;
    private String verifier;
    private String clientId;
    private Auth0ClientOptions jwtParams;
    private Boolean isNewActivity;
    private Boolean preferCustomTabs;
    private String[] allowedBrowsers;

    public SubVerifierDetails(LoginType typeOfLogin, String verifier, String clientId, Auth0ClientOptions jwtParams, boolean isNewActivity, boolean preferCustomTabs) {
        this.typeOfLogin = typeOfLogin;
        this.verifier = verifier;
        this.clientId = clientId;
        this.jwtParams = jwtParams;
        this.isNewActivity = isNewActivity;
        this.preferCustomTabs = preferCustomTabs;
    }

    public SubVerifierDetails(LoginType typeOfLogin, String verifier, String clientId, Auth0ClientOptions jwtParams, boolean isNewActivity) {
        this(typeOfLogin, verifier, clientId, jwtParams, isNewActivity, true);
    }

    public SubVerifierDetails(LoginType typeOfLogin, String verifier, String clientId) {
        this(typeOfLogin, verifier, clientId, new Auth0ClientOptions.Auth0ClientOptionsBuilder("").build(), false);
    }

    public SubVerifierDetails(LoginType typeOfLogin, String verifier, String clientId, Auth0ClientOptions jwtParams) {
        this(typeOfLogin, verifier, clientId, jwtParams, false);
    }

    public LoginType getTypeOfLogin() {
        return typeOfLogin;
    }

    public String getVerifier() {
        return verifier;
    }

    public String getClientId() {
        return clientId;
    }

    public Auth0ClientOptions getJwtParams() {
        return jwtParams;
    }

    public boolean getIsNewActivity() {
        return isNewActivity;
    }

    public boolean getPreferCustomTabs() {
        return preferCustomTabs;
    }

    public SubVerifierDetails setPreferCustomTabs(boolean val) {
        preferCustomTabs = val;
        return this;
    }

    public String[] getAllowedBrowsers() { return allowedBrowsers; }

    public SubVerifierDetails setAllowedBrowsers(String[] val) {
        allowedBrowsers = val;
        return this;
    }
}
