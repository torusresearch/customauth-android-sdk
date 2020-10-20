package org.torusresearch.torusdirect.types;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public enum LoginType {
    GOOGLE("google"),
    FACEBOOK("facebook"),
    REDDIT("reddit"),
    DISCORD("discord"),
    TWITCH("twitch"),
    APPLE("apple"),
    GITHUB("github"),
    LINKEDIN("linkedin"),
    TWITTER("twitter"),
    WEIBO("weibo"),
    LINE("line"),
    EMAIL_PASSWORD("email_password"),
    JWT("jwt");
    private static final Map<String, LoginType> BY_LABEL = new HashMap<>();

    static {
        for (LoginType e : values()) {
            BY_LABEL.put(e.label, e);
        }
    }

    private String label;

    LoginType(String label) {
        this.label = label;
    }

    public static LoginType valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @NotNull
    public String toString() {
        return label;
    }
}