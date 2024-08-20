package org.torusresearch.customauth.types;

import org.torusresearch.fetchnodedetails.types.Web3AuthNetwork;

public class CustomAuthArgs {

    // Android package redirect uri
    private final String browserRedirectUri;
    private String redirectUri;
    private Web3AuthNetwork network;
    private boolean enableOneKey;
    private String apiKey;
    private String web3AuthClientId;
    private Integer serverTimeOffset;

    public CustomAuthArgs(String browserRedirectUri, Web3AuthNetwork network, String _redirectUri, boolean enableOneKey, String apiKey, String web3AuthClientId, Integer serverTimeOffset) {
        this.redirectUri = _redirectUri;
        this.network = network;
        this.browserRedirectUri = browserRedirectUri;
        this.enableOneKey = enableOneKey;
        if (apiKey == null) {
            this.apiKey = "";
        } else {
            this.apiKey = apiKey;
        }
        this.web3AuthClientId = web3AuthClientId;
        if (serverTimeOffset == null) {
            this.serverTimeOffset = 0;
        } else {
            this.serverTimeOffset = serverTimeOffset;
        }
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

    public String getWeb3AuthClientId() {
        return web3AuthClientId;
    }

    public void setWeb3AuthClientId(String web3AuthClientId) {
        this.web3AuthClientId = web3AuthClientId;
    }

    public Integer getServerTimeOffset() {
        return serverTimeOffset;
    }
}
