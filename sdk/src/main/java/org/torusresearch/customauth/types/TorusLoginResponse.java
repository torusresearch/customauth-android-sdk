package org.torusresearch.customauth.types;

import org.torusresearch.torusutils.types.FinalKeyData;
import org.torusresearch.torusutils.types.Metadata;
import org.torusresearch.torusutils.types.SessionData;
import org.torusresearch.torusutils.types.common.TorusKey;

import java.math.BigInteger;

public class TorusLoginResponse extends TorusSingleVerifierResponse {
    private final BigInteger privateKey;
    private final String publicAddress;

    private final TorusKey retrieveKeyResponse;
    private final FinalKeyData finalKeyData;
    private final FinalKeyData oAuthKeyData;
    private final Metadata metadata;
    private final SessionData sessionData;

    public TorusLoginResponse(TorusVerifierUnionResponse userInfo, BigInteger privateKey, String publicAddress, TorusKey retrieveKeyResponse, FinalKeyData finalKeyData,
                              FinalKeyData oAuthKeyData, Metadata metadata, SessionData sessionData) {
        super(userInfo);
        this.privateKey = privateKey;
        this.publicAddress = publicAddress;
        this.retrieveKeyResponse = retrieveKeyResponse;
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

    public TorusKey getRetrieveSharesResponse() {
        return retrieveKeyResponse;
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

