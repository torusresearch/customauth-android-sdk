# Torus Direct Android SDK

This Android SDK allows you to implement DirectAuth within your android app.
It imports [fetch-node-details-java](https://github.com/torusresearch/fetch-node-details-java) to fetch information about the nodes within the Torus Network from a smart contract on Ethereum, and imports [torus-utils-java](https://github.com/torusresearch/torus-utils-java) to retrieve users' shares from the Torus Network.

The main logic implemented by this package is to fill in the key intermediate step of retrieving user-specific identifying information that is used by torus-utils-java to validate user identities to the nodes on the network.

In particular for default supported logins like Google/Facebook, they provide a unique user identification token based on the clientID that is provided.

For DirectAuth integrations, you will need to provide the clientIDs in the initialization step.

Default supported logins:
- Google
- Facebook
- Reddit
- Discord
- Twitch

```
    class DirectSdkArgs(
        val googleClientId: String?,
        val facebookClientId: String?,
        val redditClientId: String?,
        val twitchClientId: String?,
        val discordClientId: String?,
        val baseUrl: String,
        val network: EthereumNetwork?,
        val proxyContractAddress: String?
    )
```

## Custom logins

If you already have an existing login system that you wish to use DirectAuth with, you can do so
via a custom provider.

You'll need to host a separate signing server which implements a signing API which accepts AuthorizeParams,
and responds with AuthorizeResult.

The idtoken in AuthorizeResult is the raw ECDSA signature on a JSON object (SignedData).

```
// go example
//
type (
// SignedData is a data struct that needs to be ECDSA signed
	SignedData struct {
		VerifierID string  `json:"verifier_id"`
		Timestamp  big.Int `json:"timestamp"`
	}
// AuthorizeParams are the params needed for authorization.
	AuthorizeParams struct {
		VerifierID     string `json:"verifier_id"`
		VerifierIDType string `json:"verifier_id_type"`
		RedirectURI    string `json:"redirect_uri"`
		State          string `json:"state"`
		Hash           string `json:"hash"`
	}
// AuthorizeResult is the response of an authorization.
	AuthorizeResult struct {
		RedirectURI string  `json:"redirect_uri"`
		State       string  `json:"state"`
		VerifierID  string  `json:"verifier_id"`
		Timestamp   big.Int `json:"timestamp"`
		IDToken     string  `json:"idtoken"`
	}
)
```

You will also need to register your public key via a service provider (eg. Torus).

For a live example of how this works, you can visit https://alpha.tor.us, which demonstrates a custom Torus Login via email/phone number.

