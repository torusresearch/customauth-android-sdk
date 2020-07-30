package org.torusresearch.torusdirect.types;

import org.jetbrains.annotations.NotNull;

public enum Display {
    PAGE("page"),
    POPUP("popup"),
    TOUCH("touch"),
    WAP("wap");

    private String display;

    Display(String display) {
        this.display = display;
    }

    @NotNull
    public String toString() {
        return display;
    }
}
