package org.torusresearch.torusdirect.types;

public class AggregateLoginParams {
    private final AggregateVerifierType aggregateVerifierType;
    private final String verifierIdentifier;
    private final SubVerifierDetails[] subVerifierDetailsArray;

    public AggregateLoginParams(AggregateVerifierType aggregateVerifierType, String verifierIdentifier, SubVerifierDetails[] subVerifierDetailsArray) {
        this.aggregateVerifierType = aggregateVerifierType;
        this.verifierIdentifier = verifierIdentifier;
        this.subVerifierDetailsArray = subVerifierDetailsArray;
    }

    public AggregateVerifierType getAggregateVerifierType() {
        return aggregateVerifierType;
    }

    public String getVerifierIdentifier() {
        return verifierIdentifier;
    }

    public SubVerifierDetails[] getSubVerifierDetailsArray() {
        return subVerifierDetailsArray;
    }
}
