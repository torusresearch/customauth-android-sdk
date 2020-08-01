package org.torusresearch.torusdirect.types;

public class TorusVerifierUnionResponse extends TorusVerifierResponse {
    private String accessToken;
    private String idToken;

    public TorusVerifierUnionResponse(String email, String name, String profileImage, String verifier, String verifierId, LoginType typeOfLogin) {
        super(email, name, profileImage, verifier, verifierId, typeOfLogin);
    }


    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}
