package org.torusresearch.torusdirect.types;

public class TorusLoginResponse extends TorusKey {
    private final String email;
    private final String name;
    private final String profileImage;
    private final String verifier;
    private final String verifierId;

    public TorusLoginResponse(String _publicAddress, String _privateKey, String _email, String _name, String _profileImage, String _verifier, String _verifierId) {
        super(_privateKey, _publicAddress);
        this.email = _email;
        this.name = _name;
        this.profileImage = _profileImage;
        this.verifier = _verifier;
        this.verifierId = _verifierId;
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

    @Override
    public String toString() {
        return "TorusLoginResponse{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", verifier='" + verifier + '\'' +
                ", verifierId='" + verifierId + '\'' +
                '}';
    }
}
