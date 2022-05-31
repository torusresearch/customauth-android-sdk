package org.torusresearch.customauth.types;

import org.torusresearch.fetchnodedetails.FetchNodeDetails;
import org.torusresearch.fetchnodedetails.types.EthereumNetwork;

import java.util.HashMap;

public class CustomAuthArgs {

    public static HashMap<TorusNetwork, String> CONTRACT_MAP = new HashMap<TorusNetwork, String>() {{
        put(TorusNetwork.MAINNET, FetchNodeDetails.PROXY_ADDRESS_MAINNET);
        put(TorusNetwork.TESTNET, FetchNodeDetails.PROXY_ADDRESS_ROPSTEN);
        put(TorusNetwork.CYAN, FetchNodeDetails.PROXY_ADDRESS_POLYGON);
    }};

    public static HashMap<TorusNetwork, EthereumNetwork> NETWORK_MAP = new HashMap<TorusNetwork, EthereumNetwork>() {{
        put(TorusNetwork.MAINNET, EthereumNetwork.MAINNET);
        put(TorusNetwork.TESTNET, EthereumNetwork.ROPSTEN);
        put(TorusNetwork.CYAN, EthereumNetwork.POLYGON);
    }};


    public static HashMap<TorusNetwork, String> SIGNER_MAP = new HashMap<TorusNetwork, String>() {{
        put(TorusNetwork.MAINNET, "https://signer.tor.us");
        put(TorusNetwork.TESTNET, "https://signer.tor.us");
        put(TorusNetwork.CYAN, "https://signer-polygon.tor.us");
    }};


    // Android package redirect uri
    private final String browserRedirectUri;
    private String redirectUri;
    private TorusNetwork network;
    private boolean enableOneKey;


    public CustomAuthArgs(String browserRedirectUri, TorusNetwork network, String _redirectUri) {
        this.redirectUri = _redirectUri;
        this.network = network;
        this.browserRedirectUri = browserRedirectUri;
    }

    public CustomAuthArgs(String browserRedirectUri, TorusNetwork network) {
        this(browserRedirectUri, network, "");
    }

    public CustomAuthArgs(String browserRedirectUri) {
        this(browserRedirectUri, TorusNetwork.MAINNET, "");
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
}
