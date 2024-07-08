package org.torusresearch.customauth.types;

import org.torusresearch.fetchnodedetails.types.TorusNetwork;

import java.util.HashMap;

public class CustomAuthArgs {

    public static HashMap<TorusNetwork, String> SIGNER_MAP = new HashMap<TorusNetwork, String>() {{
        put(TorusNetwork.MAINNET, "https://signer.tor.us");
        put(TorusNetwork.TESTNET, "https://signer.tor.us");
        put(TorusNetwork.CYAN, "https://signer-polygon.tor.us");
        put(TorusNetwork.AQUA, "https://signer-polygon.tor.us");
    }};


    // Android package redirect uri
    private final String browserRedirectUri;
    private String redirectUri;
    private TorusNetwork network;
    private boolean enableOneKey;
    private String networkUrl;

    private String clientid;


    public CustomAuthArgs(String browserRedirectUri, TorusNetwork network, String _redirectUri, String clientid) {
        this.redirectUri = _redirectUri;
        this.network = network;
        this.browserRedirectUri = browserRedirectUri;
        this.clientid = clientid;
    }

    public CustomAuthArgs(String browserRedirectUri, TorusNetwork network) {
        this(browserRedirectUri, network, "", "");
    }

    public CustomAuthArgs(String browserRedirectUri) {
        this(browserRedirectUri, TorusNetwork.MAINNET, "", "");
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

    public String getBrowserRedirectUri() {
        return browserRedirectUri;
    }

    public boolean isEnableOneKey() {
        return enableOneKey;
    }

    public void setEnableOneKey(boolean enableOneKey) {
        this.enableOneKey = enableOneKey;
    }

    public String getNetworkUrl() {
        return networkUrl;
    }

    public void setNetworkUrl(String networkUrl) {
        this.networkUrl = networkUrl;
    }

    public String getClientId() {
        return clientid;
    }

    public void setClientId(String clientid) {
        this.clientid = clientid;
    }

}
