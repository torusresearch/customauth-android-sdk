package org.torusresearch.torusdirect.types;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public enum AggregateVerifierType {
    SINGLE_VERIFIER_ID("single_id_verifier");

    private static final Map<String, AggregateVerifierType> BY_LABEL = new HashMap<>();

    static {
        for (AggregateVerifierType e : values()) {
            BY_LABEL.put(e.label, e);
        }
    }

    private final String label;

    AggregateVerifierType(String label) {
        this.label = label;
    }

    public static AggregateVerifierType valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @NotNull
    public String toString() {
        return label;
    }
}
