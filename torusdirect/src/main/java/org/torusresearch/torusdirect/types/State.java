package org.torusresearch.torusdirect.types;

import androidx.annotation.NonNull;

public class State {
    private final String instanceId;
    private final String verifier;
    private final boolean redirectToAndroid = true;
    private final String redirectUri;

    public State(@NonNull String _instanceId, @NonNull String _verifier, @NonNull String _redirectUri) {
        this.instanceId = _instanceId;
        this.verifier = _verifier;
        this.redirectUri = _redirectUri;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getVerifier() {
        return verifier;
    }

    public String getRedirectUri() {
        return redirectUri;
    }
}
