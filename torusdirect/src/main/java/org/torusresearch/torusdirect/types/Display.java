package org.torusresearch.torusdirect.types;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public enum Display {
    PAGE("page"),
    POPUP("popup"),
    TOUCH("touch"),
    WAP("wap");

    private static final Map<String, Display> BY_LABEL = new HashMap<>();

    static {
        for (Display e : values()) {
            BY_LABEL.put(e.label, e);
        }
    }

    private String label;

    Display(String label) {
        this.label = label;
    }

    public static Display valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @NotNull
    public String toString() {
        return label;
    }
}
