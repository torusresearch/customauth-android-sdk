package org.torusresearch.torusdirectandroid;

import org.torusresearch.torusdirect.types.LoginType;

public class LoginVerifier {
    private final String name;
    private final LoginType typeOfLogin;
    private final String clientId;
    private final String verifier;
    private String domain;
    private String verifierIdField;
    private boolean isVerfierIdCaseSensitive = true;

    public LoginVerifier(String name, LoginType typeOfLogin, String clientId, String verifier) {
        this.name = name;
        this.typeOfLogin = typeOfLogin;
        this.clientId = clientId;
        this.verifier = verifier;
    }

    public LoginVerifier(String name, LoginType typeOfLogin, String clientId, String verifier, String domain) {
        this(name, typeOfLogin, clientId, verifier);
        this.domain = domain;
    }

    public LoginVerifier(String name, LoginType typeOfLogin, String clientId, String verifier, String domain, String verifierIdField, boolean isVerfierIdCaseSensitive) {
        this(name, typeOfLogin, clientId, verifier, domain);
        this.verifierIdField = verifierIdField;
        this.isVerfierIdCaseSensitive = isVerfierIdCaseSensitive;
    }

    public String getDomain() {
        return domain;
    }

    public String getVerifierIdField() {
        return verifierIdField;
    }

    public boolean isVerfierIdCaseSensitive() {
        return isVerfierIdCaseSensitive;
    }

    public String getName() {
        return name;
    }

    public LoginType getTypeOfLogin() {
        return typeOfLogin;
    }

    public String getClientId() {
        return clientId;
    }

    public String getVerifier() {
        return verifier;
    }

    @Override
    public String toString() {
        return name;
    }
}
