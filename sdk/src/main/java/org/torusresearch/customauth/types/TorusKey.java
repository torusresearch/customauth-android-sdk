package org.torusresearch.customauth.types;

import java.math.BigInteger;

public class TorusKey {
    private final BigInteger privateKey;
    private final String publicAddress;

    public TorusKey(BigInteger _privateKey, String _publicAddress) {
        this.privateKey = _privateKey;
        this.publicAddress = _publicAddress;
    }

    public BigInteger getPrivateKey() {
        return privateKey;
    }

    public String getPublicAddress() {
        return publicAddress;
    }

    @Override
    public String toString() {
        return "TorusKey{" +
                "privateKey='" + privateKey + '\'' +
                ", publicAddress='" + publicAddress + '\'' +
                '}';
    }
}
