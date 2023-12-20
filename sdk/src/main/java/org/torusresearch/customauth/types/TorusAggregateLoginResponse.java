package org.torusresearch.customauth.types;

import org.torusresearch.torusutils.types.RetrieveSharesResponse;

import java.math.BigInteger;

public class TorusAggregateLoginResponse extends TorusAggregateVerifierResponse {
    private final BigInteger privateKey;
    private final String publicAddress;
    private final RetrieveSharesResponse retrieveSharesResponse;

    public TorusAggregateLoginResponse(TorusVerifierUnionResponse[] userInfo, BigInteger privateKey, String publicAddress, RetrieveSharesResponse retrieveSharesResponse) {
        super(userInfo);
        this.privateKey = privateKey;
        this.publicAddress = publicAddress;
        this.retrieveSharesResponse = retrieveSharesResponse;
    }

    public BigInteger getPrivateKey() {
        return privateKey;
    }

    public String getPublicAddress() {
        return publicAddress;
    }

    public RetrieveSharesResponse getRetrieveSharesResponse() {
        return retrieveSharesResponse;
    }
}
