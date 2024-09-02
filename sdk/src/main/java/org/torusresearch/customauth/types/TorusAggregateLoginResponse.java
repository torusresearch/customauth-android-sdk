package org.torusresearch.customauth.types;

import org.torusresearch.torusutils.types.common.TorusKey;

import java.math.BigInteger;

public class TorusAggregateLoginResponse extends TorusAggregateVerifierResponse {
    private final BigInteger privateKey;
    private final String publicAddress;
    private final TorusKey retrieveKeyResponse;

    public TorusAggregateLoginResponse(TorusVerifierUnionResponse[] userInfo, BigInteger privateKey, String publicAddress, TorusKey retrieveKeyResponse) {
        super(userInfo);
        this.privateKey = privateKey;
        this.publicAddress = publicAddress;
        this.retrieveKeyResponse = retrieveKeyResponse;
    }

    public BigInteger getPrivateKey() {
        return privateKey;
    }

    public String getPublicAddress() {
        return publicAddress;
    }

    public TorusKey getRetrieveSharesResponse() {
        return retrieveKeyResponse;
    }
}
