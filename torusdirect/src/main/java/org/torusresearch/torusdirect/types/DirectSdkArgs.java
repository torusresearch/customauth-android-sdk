package org.torusresearch.torusdirect.types;

public class DirectSdkArgs {
    // Android package redirect uri
    private final String redirectUri;
    private final TorusNetwork network;
    private final String proxyContractAddress;
    private String browserRedirectUri;

    public DirectSdkArgs(String _redirectUri, TorusNetwork network, String proxyContractAddress) {
        this(_redirectUri, network, proxyContractAddress, "https://scripts.toruswallet.io/redirect.html");
    }

    public DirectSdkArgs(String _redirectUri, TorusNetwork network, String proxyContractAddress, String browserRedirectUri) {
        this.redirectUri = _redirectUri;
        this.network = network;
        this.proxyContractAddress = proxyContractAddress;
        this.browserRedirectUri = browserRedirectUri;
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

    public String getBrowserRedirectUri() {
        return browserRedirectUri;
    }
}
