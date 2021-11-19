package org.torusresearch.customauth.types;

public class NoAllowedBrowserFoundException extends Exception {
    public NoAllowedBrowserFoundException() {
        super("No allowed browser found.");
    }
}
