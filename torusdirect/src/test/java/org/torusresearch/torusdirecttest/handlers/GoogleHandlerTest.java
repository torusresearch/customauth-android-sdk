package org.torusresearch.torusdirecttest.handlers;

import org.junit.jupiter.api.Test;
import org.torusresearch.torusdirect.handlers.GoogleHandler;

import java.util.UUID;

public class GoogleHandlerTest {

    @Test
    public void getsCorrectFinalUrl() {
        GoogleHandler handler = new GoogleHandler(UUID.randomUUID().toString(), "abcd-123", "https://app.tor.us/redirect", "google");
        System.out.println(handler.getFinalUrl());
    }
}
