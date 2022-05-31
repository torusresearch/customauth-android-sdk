package org.torusresearch.customauth.types;

import java.math.BigInteger;

public class TorusAggregateLoginResponse extends TorusAggregateVerifierResponse {
    private final BigInteger privateKey;
    private final String publicAddress;

    public TorusAggregateLoginResponse(TorusVerifierUnionResponse[] userInfo, BigInteger privateKey, String publicAddress) {
        super(userInfo);
        this.privateKey = privateKey;
        this.publicAddress = publicAddress;
    }

    public BigInteger getPrivateKey() {
        return privateKey;
    }

    public String getPublicAddress() {
        return publicAddress;
    }
}
