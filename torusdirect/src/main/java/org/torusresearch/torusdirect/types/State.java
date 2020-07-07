package org.torusresearch.torusdirect.types;

import androidx.annotation.NonNull;

public class State {
    private final String instanceId;
    private final String verifier;

    public State(@NonNull String _instanceId, @NonNull String _verifier) {
        this.instanceId = _instanceId;
        this.verifier = _verifier;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getVerifier() {
        return verifier;
    }
}
