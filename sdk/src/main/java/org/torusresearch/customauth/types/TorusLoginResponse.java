package org.torusresearch.customauth.types;

import java.math.BigInteger;

public class TorusLoginResponse extends TorusSingleVerifierResponse {
    private final BigInteger privateKey;
    private final String publicAddress;

    public TorusLoginResponse(TorusVerifierUnionResponse userInfo, BigInteger privateKey, String publicAddress) {
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

