package org.torusresearch.customauth.types;

public class TorusLoginResponse extends TorusSingleVerifierResponse {
    private final String privateKey;
    private final String publicAddress;

    public TorusLoginResponse(TorusVerifierUnionResponse userInfo, String privateKey, String publicAddress) {
        super(userInfo);
        this.privateKey = privateKey;
        this.publicAddress = publicAddress;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPublicAddress() {
        return publicAddress;
    }
}

