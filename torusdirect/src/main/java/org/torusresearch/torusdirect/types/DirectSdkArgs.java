package org.torusresearch.torusdirect.types;

import org.torusresearch.fetchnodedetails.types.EthereumNetwork;

public class DirectSdkArgs {
    private final String redirectUri;
    private String googleClientId;
    private String facebookClientId;
    private String redditClientId;
    private String twitchClientId;
    private String discordClientId;
    private EthereumNetwork network;
    private String proxyContractAddress;

    public DirectSdkArgs(String _redirectUri) {
        this.redirectUri = _redirectUri;
    }

    public String getGoogleClientId() {
        return googleClientId;
    }

    public void setGoogleClientId(String googleClientId) {
        this.googleClientId = googleClientId;
    }

    public String getRedditClientId() {
        return redditClientId;
    }

    public void setRedditClientId(String redditClientId) {
        this.redditClientId = redditClientId;
    }

    public String getTwitchClientId() {
        return twitchClientId;
    }

    public void setTwitchClientId(String twitchClientId) {
        this.twitchClientId = twitchClientId;
    }

    public String getDiscordClientId() {
        return discordClientId;
    }

    public void setDiscordClientId(String discordClientId) {
        this.discordClientId = discordClientId;
    }

    public String getFacebookClientId() {
        return facebookClientId;
    }

    public void setFacebookClientId(String facebookClientId) {
        this.facebookClientId = facebookClientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public EthereumNetwork getNetwork() {
        return network;
    }

    public void setNetwork(EthereumNetwork network) {
        this.network = network;
    }

    public String getProxyContractAddress() {
        return proxyContractAddress;
    }

    public void setProxyContractAddress(String proxyContractAddress) {
        this.proxyContractAddress = proxyContractAddress;
    }

    @Override
    public String toString() {
        return "DirectSdkArgs{" +
                "googleClientId='" + googleClientId + '\'' +
                ", facebookClientId='" + facebookClientId + '\'' +
                ", redditClientId='" + redditClientId + '\'' +
                ", twitchClientId='" + twitchClientId + '\'' +
                ", discordClientId='" + discordClientId + '\'' +
                ", redirectUri='" + redirectUri + '\'' +
                ", network=" + network +
                ", proxyContractAddress='" + proxyContractAddress + '\'' +
                '}';
    }
}
