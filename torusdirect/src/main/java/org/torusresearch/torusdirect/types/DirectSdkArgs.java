package org.torusresearch.torusdirect.types;

import java.util.HashMap;

public class DirectSdkArgs {

    public static HashMap<TorusNetwork, String> CONTRACT_MAP = new HashMap<TorusNetwork, String>() {{
        put(TorusNetwork.MAINNET, "0x638646503746d5456209e33a2ff5e3226d698bea");
        put(TorusNetwork.TESTNET, "0x4023d2a0D330bF11426B12C6144Cfb96B7fa6183");
    }};
    // Android package redirect uri
    private final String browserRedirectUri;
    private String redirectUri;
    private TorusNetwork network;
    private String proxyContractAddress;

    public DirectSdkArgs(String browserRedirectUri, TorusNetwork network, String _redirectUri) {
        this(browserRedirectUri, network, _redirectUri, CONTRACT_MAP.get(network));
    }

    public DirectSdkArgs(String browserRedirectUri, TorusNetwork network, String _redirectUri, String proxyContractAddress) {
        this.redirectUri = _redirectUri;
        this.network = network;
        this.proxyContractAddress = proxyContractAddress;
        this.browserRedirectUri = browserRedirectUri;
    }

    public DirectSdkArgs(String browserRedirectUri, TorusNetwork network) {
        this(browserRedirectUri, network, "", CONTRACT_MAP.get(network));
    }

    public DirectSdkArgs(String browserRedirectUri) {
        this(browserRedirectUri, TorusNetwork.MAINNET, "", CONTRACT_MAP.get(TorusNetwork.MAINNET));
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public TorusNetwork getNetwork() {
        return network;
    }

    public void setNetwork(TorusNetwork network) {
        this.network = network;
    }

    public String getProxyContractAddress() {
        return proxyContractAddress;
    }

    public void setProxyContractAddress(String proxyContractAddress) {
        this.proxyContractAddress = proxyContractAddress;
    }

    public String getBrowserRedirectUri() {
        return browserRedirectUri;
    }
}
