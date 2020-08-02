package org.torusresearch.torusdirect.types;

public class TorusAggregateLoginResponse extends TorusAggregateVerifierResponse {
    private final String privateKey;
    private final String publicAddress;

    public TorusAggregateLoginResponse(TorusVerifierUnionResponse[] userInfo, String privateKey, String publicAddress) {
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
