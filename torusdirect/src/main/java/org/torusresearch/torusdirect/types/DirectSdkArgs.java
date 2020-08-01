package org.torusresearch.torusdirect.types;

public class DirectSdkArgs {
    private final String redirectUri;
    private final TorusNetwork network;
    private final String proxyContractAddress;

    public DirectSdkArgs(String _redirectUri, TorusNetwork network, String proxyContractAddress) {
        this.redirectUri = _redirectUri;
        this.network = network;
        this.proxyContractAddress = proxyContractAddress;
    }

    public DirectSdkArgs(String _redirectUri) {
        this(_redirectUri, TorusNetwork.MAINNET, "0x638646503746d5456209e33a2ff5e3226d698bea");
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public TorusNetwork getNetwork() {
        return network;
    }

    public String getProxyContractAddress() {
        return proxyContractAddress;
    }

}
