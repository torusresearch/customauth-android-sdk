package org.torusresearch.torusdirect.types;

public class TorusSubVerifierInfo {
    String verifier;
    String idToken;

    public TorusSubVerifierInfo(String verifier, String idToken) {
        this.verifier = verifier;
        this.idToken = idToken;
    }

    public String getVerifier() {
        return verifier;
    }

    public String getIdToken() {
        return idToken;
    }
}
