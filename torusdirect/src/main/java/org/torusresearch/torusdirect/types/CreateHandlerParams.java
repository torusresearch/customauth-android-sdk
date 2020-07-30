package org.torusresearch.torusdirect.types;

public class CreateHandlerParams extends LoginHandlerParams {
    private Auth0ClientOptions jwtParams;

    public CreateHandlerParams(String clientId, String verifier, String redirect_uri, LoginType typeOfLogin) {
        super(clientId, verifier, redirect_uri, typeOfLogin);
    }

    public CreateHandlerParams(String clientId, String verifier, String redirect_uri, LoginType typeOfLogin, Auth0ClientOptions jwtParams) {
        super(clientId, verifier, redirect_uri, typeOfLogin);
        this.jwtParams = jwtParams;
    }

    public Auth0ClientOptions getJwtParams() {
        return jwtParams;
    }
}
