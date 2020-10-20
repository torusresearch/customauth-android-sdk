package org.torusresearch.torusdirect.types;

import org.jetbrains.annotations.NotNull;

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
    private String loginType;

    LoginType(String loginType) {
        this.loginType = loginType;
    }

    @NotNull
    public String toString() {
        return loginType;
    }
}
