package org.torusresearch.torusdirect.types;

public class NoAllowedBrowserFoundException extends Exception {
    public NoAllowedBrowserFoundException() {
        super("No allowed browser found.");
    }
}
