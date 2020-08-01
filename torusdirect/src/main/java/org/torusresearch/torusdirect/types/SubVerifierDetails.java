package org.torusresearch.torusdirect.types;

public class SubVerifierDetails {
    private LoginType typeOfLogin;
    private String verifier;
    private String clientId;
    private Auth0ClientOptions jwtParams;

    public SubVerifierDetails(LoginType typeOfLogin, String verifier, String clientId, Auth0ClientOptions jwtParams) {
        this.typeOfLogin = typeOfLogin;
        this.verifier = verifier;
        this.clientId = clientId;
        this.jwtParams = jwtParams;
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
}
