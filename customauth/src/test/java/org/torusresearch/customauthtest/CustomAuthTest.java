package org.torusresearch.customauthtest;

import org.junit.jupiter.api.Test;
import org.torusresearch.customauth.CustomAuth;
import org.torusresearch.customauth.types.CustomAuthArgs;

public class CustomAuthTest {

    @Test
    public void ctorInitialization() {
        CustomAuthArgs args = new CustomAuthArgs("https://localhost:3000/redirect");
        CustomAuth sdk = new CustomAuth(args, null);
    }
}
