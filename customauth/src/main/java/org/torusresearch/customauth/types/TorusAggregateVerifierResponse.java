package org.torusresearch.customauth.types;

public class TorusAggregateVerifierResponse {
    private final TorusVerifierUnionResponse[] userInfo;

    public TorusAggregateVerifierResponse(TorusVerifierUnionResponse[] userInfo) {
        this.userInfo = userInfo;
    }

    public TorusVerifierUnionResponse[] getUserInfo() {
        return userInfo;
    }
}
