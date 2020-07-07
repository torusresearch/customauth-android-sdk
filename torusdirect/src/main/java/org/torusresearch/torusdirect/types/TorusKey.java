package org.torusresearch.torusdirect.types;

public class TorusKey {
    private final String privateKey;
    private final String publicAddress;

    public TorusKey(String _privateKey, String _publicAddress) {
        this.privateKey = _privateKey;
        this.publicAddress = _publicAddress;
    }

    public String getPrivateKey() {
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
