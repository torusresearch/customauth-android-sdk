package org.torusresearch.torusdirect.types;

public final class JwtUserInfoResult {
    private final String picture;
    private final String email;
    private final String name;
    private final String sub;
    private final String nickname;

    public JwtUserInfoResult(String picture, String email, String name, String sub, String nickname) {
        this.picture = picture;
        this.email = email;
        this.name = name;
        this.sub = sub;
        this.nickname = nickname;
    }

    public String getSub() {
        return sub;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPicture() {
        return picture;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
