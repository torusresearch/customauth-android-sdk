package org.torusresearch.customauth.types;

import org.torusresearch.fetchnodedetails.types.Web3AuthNetwork;

import java.util.HashMap;

public class CustomAuthArgs {

    public static HashMap<Web3AuthNetwork, String> SIGNER_MAP = new HashMap<Web3AuthNetwork, String>() {{
        put(Web3AuthNetwork.MAINNET, "https://signer.tor.us");
        put(Web3AuthNetwork.TESTNET, "https://signer.tor.us");
        put(Web3AuthNetwork.CYAN, "https://signer-polygon.tor.us");
        put(Web3AuthNetwork.AQUA, "https://signer-polygon.tor.us");
        put(Web3AuthNetwork.SAPPHIRE_MAINNET, "https://signer.tor.us");
        put(Web3AuthNetwork.SAPPHIRE_DEVNET, "https://signer.tor.us");
    }};

    // Android package redirect uri
    private final String browserRedirectUri;
    private String redirectUri;
    private Web3AuthNetwork network;
    private boolean enableOneKey;
    private String networkUrl;
    private String clientId;

    private String clientId;


    public CustomAuthArgs(String browserRedirectUri, Web3AuthNetwork network, String _redirectUri, String clientId) {
        this.redirectUri = _redirectUri;
        this.network = network;
        this.browserRedirectUri = browserRedirectUri;
        this.clientId = clientId;
    }

    public CustomAuthArgs(String browserRedirectUri, Web3AuthNetwork network, String clientId) {
        this(browserRedirectUri, network, "", clientId);
    }

    public CustomAuthArgs(String browserRedirectUri, String clientId) {
        this(browserRedirectUri, Web3AuthNetwork.MAINNET, "", clientId);
    }

    public CustomAuthArgs(String browserRedirectUri, TorusNetwork network, String _redirectUri, String clientId) {
        this.redirectUri = _redirectUri;
        this.network = network;
        this.browserRedirectUri = browserRedirectUri;
        this.clientId = clientId;
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

    public String getNetworkUrl() {
        return networkUrl;
    }

    public void setNetworkUrl(String networkUrl) {
        this.networkUrl = networkUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientid) {
        this.clientId = clientid;
    }
}
