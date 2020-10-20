package org.torusresearch.torusdirect.types;

import org.jetbrains.annotations.NotNull;

public enum TorusNetwork {
    MAINNET("mainnet"),
    TESTNET("testnet");

    private String network;

    TorusNetwork(String network) {
        this.network = network;
    }

    @NotNull
    public String toString() {
        return network;
    }
}
