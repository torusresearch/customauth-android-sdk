package org.torusresearch.customauth.types;

import java.math.BigInteger;

public class TorusLoginResponse extends TorusSingleVerifierResponse {
    private final String privateKey;
    private final String publicAddress;

    private final BigInteger nonce;

    private final String typeOfUser;

    public TorusLoginResponse(TorusVerifierUnionResponse userInfo, String privateKey, String publicAddress, BigInteger nonce, String  typeOfUser) {
        super(userInfo);
        this.privateKey = privateKey;
        this.publicAddress = publicAddress;
        this.nonce = nonce;
        this.typeOfUser = typeOfUser;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPublicAddress() {
        return publicAddress;
    }

    private BigInteger getNonce() {
        return nonce;
    }

    public String getTypeOfUser() {
        return typeOfUser;
    }


}

