package org.torusresearch.torusdirect.types;

public class TorusVerifierResponse {
    private final String email;
    private final String name;
    private final String profileImage;
    private final String verifier;
    private final String verifierId;
    private final LoginType typeOfLogin;

    public TorusVerifierResponse(String email, String name, String profileImage, String verifier, String verifierId, LoginType typeOfLogin) {
        this.email = email;
        this.name = name;
        this.profileImage = profileImage;
        this.verifier = verifier;
        this.verifierId = verifierId;
        this.typeOfLogin = typeOfLogin;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getVerifier() {
        return verifier;
    }

    public String getVerifierId() {
        return verifierId;
    }

    public LoginType getTypeOfLogin() {
        return typeOfLogin;
    }
}
