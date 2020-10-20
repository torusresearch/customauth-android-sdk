package org.torusresearch.torusdirect.types;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public enum TorusNetwork {
    MAINNET("mainnet"),
    TESTNET("testnet");

    private static final Map<String, TorusNetwork> BY_LABEL = new HashMap<>();

    static {
        for (TorusNetwork e : values()) {
            BY_LABEL.put(e.label, e);
        }
    }

    private String label;

    TorusNetwork(String label) {
        this.label = label;
    }

    public static TorusNetwork valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @NotNull
    public String toString() {
        return label;
    }
}
