package org.torusresearch.torusdirecttest;

import org.junit.jupiter.api.Test;
import org.torusresearch.torusdirect.TorusDirectSdk;
import org.torusresearch.torusdirect.types.DirectSdkArgs;

public class TorusDirectSdkTest {

    @Test
    public void ctorInitialization() {
        DirectSdkArgs args = new DirectSdkArgs("https://localhost:3000/redirect");
        TorusDirectSdk sdk = new TorusDirectSdk(args, null);
    }
}
