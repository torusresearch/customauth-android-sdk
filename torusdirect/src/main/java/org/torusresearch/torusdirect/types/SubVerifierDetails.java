package org.torusresearch.torusdirect.types;

public class SubVerifierDetails {
    private LoginType typeOfLogin;
    private String verifier;
    private String clientId;
    private Auth0ClientOptions jwtParams;
    private Boolean isNewActivity;

    public SubVerifierDetails(LoginType typeOfLogin, String verifier, String clientId, Auth0ClientOptions jwtParams, boolean isNewActivity) {
        this.typeOfLogin = typeOfLogin;
        this.verifier = verifier;
        this.clientId = clientId;
        this.jwtParams = jwtParams;
        this.isNewActivity = isNewActivity;
    }

    public SubVerifierDetails(LoginType typeOfLogin, String verifier, String clientId) {
        this(typeOfLogin, verifier, clientId, new Auth0ClientOptions.Auth0ClientOptionsBuilder("").build(), false);
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
}
