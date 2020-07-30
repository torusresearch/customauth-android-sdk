package org.torusresearch.torusdirect.types;

import org.jetbrains.annotations.NotNull;

public enum Prompt {
    NONE("none"),
    LOGIN("login"),
    CONSENT("consent"),
    SELECT_ACCOUNT("select_account");

    private String prompt;

    Prompt(String prompt) {
        this.prompt = prompt;
    }

    @NotNull
    public String toString() {
        return prompt;
    }
}
