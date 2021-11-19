package org.torusresearch.customauth.types;

public class UserCancelledException extends Exception {
    public UserCancelledException() {
        super("User cancelled.");
    }
}
