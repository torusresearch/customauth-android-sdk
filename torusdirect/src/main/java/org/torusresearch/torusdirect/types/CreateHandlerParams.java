package org.torusresearch.torusdirect.types;

public class CreateHandlerParams extends LoginHandlerParams {
    private Auth0ClientOptions jwtParams = new Auth0ClientOptions.Auth0ClientOptionsBuilder("").build();

    public CreateHandlerParams(String clientId, String verifier, String redirect_uri, LoginType typeOfLogin, String browserRedirectUri) {
        super(clientId, verifier, redirect_uri, typeOfLogin, browserRedirectUri);
    }

    public CreateHandlerParams(String clientId, String verifier, String redirect_uri, LoginType typeOfLogin, String browserRedirectUri, Auth0ClientOptions jwtParams) {
        super(clientId, verifier, redirect_uri, typeOfLogin, browserRedirectUri);
        this.jwtParams = jwtParams;
    }

    public Auth0ClientOptions getJwtParams() {
        return jwtParams;
    }
}
