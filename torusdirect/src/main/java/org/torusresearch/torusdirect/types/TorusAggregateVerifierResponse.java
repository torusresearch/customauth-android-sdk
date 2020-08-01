package org.torusresearch.torusdirect.types;

public class TorusAggregateVerifierResponse {
    private final TorusVerifierUnionResponse[] userInfo;

    public TorusAggregateVerifierResponse(TorusVerifierUnionResponse[] userInfo) {
        this.userInfo = userInfo;
    }

    public TorusVerifierUnionResponse[] getUserInfo() {
        return userInfo;
    }
}
