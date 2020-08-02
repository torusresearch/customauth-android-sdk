package org.torusresearch.torusdirect.types;

public class LoginWindowResponse {
    private String accessToken;
    private String idToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public void parseResponse(String response) {
        String[] params = response.substring(response.indexOf("#") + 1).split("&");
        for (String param : params) {
            String[] keyValuePair = param.split("=");
            if (keyValuePair.length > 0) {
                if (keyValuePair[0].equalsIgnoreCase("access_token")) {
                    setAccessToken(keyValuePair[1]);
                } else if (keyValuePair[0].equalsIgnoreCase("id_token")) {
                    setIdToken(keyValuePair[1]);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "LoginWindowResponse{" +
                "accessToken='" + accessToken + '\'' +
                ", idToken='" + idToken + '\'' +
                '}';
    }
}
