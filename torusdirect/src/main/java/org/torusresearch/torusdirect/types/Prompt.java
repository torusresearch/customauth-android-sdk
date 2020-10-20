package org.torusresearch.torusdirect.types;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public enum Prompt {
    NONE("none"),
    LOGIN("login"),
    CONSENT("consent"),
    SELECT_ACCOUNT("select_account");

    private static final Map<String, Prompt> BY_LABEL = new HashMap<>();

    static {
        for (Prompt e : values()) {
            BY_LABEL.put(e.label, e);
        }
    }

    private String label;

    Prompt(String label) {
        this.label = label;
    }

    public static Prompt valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @NotNull
    public String toString() {
        return label;
    }
}
