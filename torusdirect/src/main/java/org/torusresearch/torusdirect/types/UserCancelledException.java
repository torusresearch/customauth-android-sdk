package org.torusresearch.torusdirect.types;

public class UserCancelledException extends Exception {
    public UserCancelledException() {
        super("User cancelled.");
    }
}
