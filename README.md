# customauth-android-sdk

[![](https://jitpack.io/v/org.torusresearch/customauth-android-sdk.svg)](https://jitpack.io/#org.torusresearch/customauth-android-sdk)

## Introduction

Torus CustomAuth SDK for Android applications.

## Installation

Typically your application should depend on release versions of
customauth-android-sdk, but you may also use snapshot dependencies for early
access to features and fixes, refer to the Snapshot Dependencies section. This
project uses [jitpack](https://jitpack.io/docs/) for release management

Add the relevant dependency to your project:

```groovy
repositories {
        maven { url "https://jitpack.io" }
   }
   dependencies {
         implementation 'org.torusresearch:customauth-android-sdk:3.0.2'
   }
```

## 🩹 Examples

Checkout the example of `CustomAuth Android SDK` in our
[examples directory.](https://github.com/torusresearch/customauth-android-sdk/tree/master/app)

## Usage

To allow your web app to retrieve keys:

1. Install the package

2. At verifier's interface (where you obtain client id), please use
   `browserRedirectUri` in DirectSdkArgs as the redirect uri. e.g:
   browserRedirectUri can be `YOUR_APP_DEEP_LINK` if the OAuth provider supports
   it. else, follow the next step If you specify a custom `browserRedirectUri`
   or OAuth provider doesn't support deep link url, pls host
   [redirect.html](customauth/src/main/java/org/torusresearch/customauth/activity/redirect.html)
   at that `browserRedirectUri` after editing `whiteListedURLs` in
   [redirect.html](customauth/src/main/java/org/torusresearch/customauth/activity/redirect.html)
   with the scheme specified in manifestPlaceHolders and pass in as
   `redirectUri`.

3. Register the startup activity in the manifest file using manifest placeholder
   in build.gradle file (when a custom scheme is used)

```groovy
android.defaultConfig.manifestPlaceholders = [
        'torusRedirectScheme': 'YOUR_APP_SCHEME', // (torusapp)
        'torusRedirectHost': 'YOUR_APP_HOST', // (org.torusresearch.customauthandroid)
        'torusRedirectPathPrefix': 'YOUR_REDIRECT_PATH' // (/redirect)
]
```

or

```xml
<activity android:name="org.torusresearch.customauth.activity.StartUpActivity"
    android:launchMode="singleTop">
    <intent-filter>
        <action android:name="android.intent.action.VIEW"/>
        <category android:name="android.intent.category.DEFAULT"/>
        <category android:name="android.intent.category.BROWSABLE"/>
        <data android:scheme="YOUR_APP_SCHEME"
            android:host="YOUR_APP_HOST"
            android:pathPattern="/*"
            android:pathPrefix="YOUR_REDIRECT_PATH"/>
    </intent-filter>
</activity>
```

4. Instantiate the package with your own specific client-id and
   browserRedirectUri as `YOUR_APP_SCHEME://YOUR_APP_HOST/YOUR_REDIRECT_PATH` (
   eg:`torusapp://org.torusresearch.customauthandroid/redirect`)

5. Trigger the login

6. Reach out to hello@tor.us to get your verifier spun up on the testnet today!

## Proguard

No Proguard configuration is required. SDK will automatically append necessary
rules to the project's proguard-rules.txt file.

## Features

- All API's return `CompletableFutures`
- Example included

## Generating ED25519 key

- By default sdk will return `secpk256k1` key, you can retrieve ed25519 keys
  passing the `secpk256k1` key as seed in `tweetnacl` sdk `keypair_fromSeed`
  function.

- Refer to code snippet given below or app folder in this repo for an example
  function to generate an account using solana sdk which uses `ed25519` keys.

```java

    // privateKey is the key which you will get after user's login from customauth-android-sdk
    public TweetNaclFast.Signature.KeyPair getEd25199Key(String privateKey) {
        byte[] decodedBytes =  TweetNaclFast.hexDecode(privateKey);
        TweetNaclFast.Signature.KeyPair ed25519KeyPair = TweetNaclFast.Signature.keyPair_fromSeed(decodedBytes);
        return  ed25519KeyPair;
    }

    public void  createSolanaAccount(View view) {
        TextView textView = findViewById(R.id.output);

        if (this.privKey.isEmpty()) {
            textView.setText("Please login first to generate solana ed25519 key pair");
            return;
        }
        TweetNaclFast.Signature.KeyPair ed25519KeyPair = this.getEd25199Key(this.privKey);
        Account SolanaAccount = new Account(ed25519KeyPair.getSecretKey());
        String pubKey = SolanaAccount.getPublicKey().toBase58();
        String secretKey = Base58.encode(SolanaAccount.getSecretKey());
        String accountInfo = String.format("Solana account secret key is %s and public Key %s",secretKey, pubKey);
        textView.setText(accountInfo);
    }

```

## Info

The following links help you create OAuth accounts with different login
providers

- [Google](https://support.google.com/googleapi/answer/6158849)
- [Facebook](https://developers.facebook.com/docs/apps)
- [Reddit](https://github.com/reddit-archive/reddit/wiki/oauth2)
- [Twitch](https://dev.twitch.tv/docs/authentication/#registration)
- [Discord](https://discord.com/developers/docs/topics/oauth2)

For other verifiers,

- you'll need to create an [Auth0 account](https://auth0.com/)
- [create an application](https://auth0.com/docs/connections) for the login type
  you want
- Pass in the clientId, domain of the Auth0 application into the torus login
  request

## Best practices

- Please run the entire sdk calls in a new threadpool. Refer to example for
  basic configuration.

## FAQ

1. **Question:** Discord Login only works once in 30 min

   **Answer:** Torus Login requires a new token for every login attempt. Discord
   returns the same access token for 30 min unless it's revoked. Unfortunately,
   it needs to be revoked from the backend since it needs a client secret.
   Here's some sample code which does it

   ```js
   const axios = require('axios').default
   const FormData = require('form-data')

   const {DISCORD_CLIENT_SECRET, DISCORD_CLIENT_ID} = process.env
   const {token} = req.body
   const formData = new FormData()
   formData.append('token', token)
   await axios.post(
     'https://discordapp.com/api/oauth2/token/revoke',
     formData,
     {
       headers: {
         ...formData.getHeaders(),
         Authorization: `Basic ${Buffer.from(
           `${DISCORD_CLIENT_ID}:${DISCORD_CLIENT_SECRET}`,
           'binary',
         ).toString('base64')}`,
       },
     },
   )
   ```

2. **Question:** How to initialise web3 with private key (returned after login)
   ?

   **Answer:** Use web3j

## Requirements

- Android - API level 24
- Java 8

## 💬 Troubleshooting and Discussions

- Have a look at our
  [GitHub Discussions](https://github.com/Web3Auth/Web3Auth/discussions?discussions_q=sort%3Atop)
  to see if anyone has any questions or issues you might be having.
- Checkout our
  [Troubleshooting Documentation Page](https://web3auth.io/docs/troubleshooting)
  to know the common issues and solutions
- Join our [Discord](https://discord.gg/web3auth) to join our community and get
  private integration support or help with your integration.
