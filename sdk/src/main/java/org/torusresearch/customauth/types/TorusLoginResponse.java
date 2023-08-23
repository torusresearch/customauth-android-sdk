package org.torusresearch.customauth.types;

import org.torusresearch.torusutils.types.FinalKeyData;
import org.torusresearch.torusutils.types.Metadata;
import org.torusresearch.torusutils.types.RetrieveSharesResponse;
import org.torusresearch.torusutils.types.SessionData;

import java.math.BigInteger;

public class TorusLoginResponse extends TorusSingleVerifierResponse {
    private final BigInteger privateKey;
    private final String publicAddress;

    private final RetrieveSharesResponse retrieveSharesResponse;
    private final FinalKeyData finalKeyData;
    private final FinalKeyData oAuthKeyData;
    private final Metadata metadata;
    private final SessionData sessionData;

    public TorusLoginResponse(TorusVerifierUnionResponse userInfo, BigInteger privateKey, String publicAddress, RetrieveSharesResponse retrieveSharesResponse, FinalKeyData finalKeyData,
                              FinalKeyData oAuthKeyData, Metadata metadata, SessionData sessionData) {
        super(userInfo);
        this.privateKey = privateKey;
        this.publicAddress = publicAddress;
        this.retrieveSharesResponse = retrieveSharesResponse;
        this.finalKeyData = finalKeyData;
        this.oAuthKeyData = oAuthKeyData;
        this.metadata = metadata;
        this.sessionData = sessionData;
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

    public FinalKeyData getFinalKeyData() {
        return finalKeyData;
    }

    public FinalKeyData getoAuthKeyData() {
        return oAuthKeyData;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public SessionData getSessionData() {
        return sessionData;
    }

}

