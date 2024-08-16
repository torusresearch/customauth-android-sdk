package org.torusresearch.customauth.types;

import org.torusresearch.fetchnodedetails.types.Web3AuthNetwork;

public class CustomAuthArgs {

    // Android package redirect uri
    private final String browserRedirectUri;
    private String redirectUri;
    private Web3AuthNetwork network;
    private boolean enableOneKey;
    private Web3AuthNetwork networkUrl;
    private String clientId;


    public CustomAuthArgs(String browserRedirectUri, Web3AuthNetwork network, String _redirectUri, String clientId, boolean enableOneKey) {
        this.redirectUri = _redirectUri;
        this.network = network;
        this.browserRedirectUri = browserRedirectUri;
        this.clientId = clientId;
        this.enableOneKey = enableOneKey;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public Web3AuthNetwork getNetwork() {
        return network;
    }

    public void setNetwork(Web3AuthNetwork network) {
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

    public Web3AuthNetwork getNetworkUrl() {
        return networkUrl;
    }

    public void setNetworkUrl(Web3AuthNetwork networkUrl) {
        this.networkUrl = networkUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientid) {
        this.clientId = clientid;
    }
}
