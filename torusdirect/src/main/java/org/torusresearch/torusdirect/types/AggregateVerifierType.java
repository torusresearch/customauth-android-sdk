package org.torusresearch.torusdirect.types;

import org.jetbrains.annotations.NotNull;

public enum AggregateVerifierType {
    SINGLE_VERIFIER_ID("single_verifier_id");

    private String verifierType;

    AggregateVerifierType(String verifierType) {
        this.verifierType = verifierType;
    }

    @NotNull
    public String toString() {
        return verifierType;
    }
}
