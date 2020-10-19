# Torus-direct-android-sdk

[![](https://jitpack.io/v/org.torusresearch/torus-direct-android-sdk.svg)](https://jitpack.io/#org.torusresearch/torus-direct-android-sdk)

## Introduction

This repo allows web applications to directly retrieve keys stored on the Torus Network. The attestation layer for the Torus Network is generalizable, below is an example of how to access keys via the SDK via Google.

## Features

- All API's return `CompletableFutures`
- Example included

## Installation

Typically your application should depend on release versions of torus-direct-android-sdk, but you may also use snapshot dependencies for early access to features and fixes, refer to the Snapshot Dependencies section.
This project uses [jitpack](https://jitpack.io/docs/) for release management

Add the relevant dependency to your project:

```groovy
repositories {
        maven { url "https://jitpack.io" }
   }
   dependencies {
         implementation 'org.torusresearch:torus-direct-android-sdk:1.0.0'
   }
```

## Usage

To allow your web app to retrieve keys:

1. Install the package

2. At verifier's interface (where you obtain client id), please use `browserRedirectUri` in DirectSdkArgs (default: 'https://scripts.toruswallet.io/redirect.html')
 as the redirect uri. If you specify a custom `browserRedirectUri`, pls host [redirect.html](torusdirect/src/main/java/org/torusresearch/torusdirect/activity/redirect.html) at that url.
 
3. 

Register the startup activity in the manifest file

using manifest placeholder in build.gradle file (when a custom scheme is used)

```groovy
android.defaultConfig.manifestPlaceholders = [
        'torusRedirectScheme': 'YOUR_APP_SCHEME', // (torusapp)
        'torusRedirectHost': 'YOUR_APP_HOST', // (org.torusresearch.torusdirectandroid)
        'torusRedirectPathPrefix': 'YOUR_REDIRECT_PATH' // (/redirect)
]
```
or

```xml
<activity android:name="org.torusresearch.torusdirect.activity.StartUpActivity"
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

3. Instantiate the package with your own specific client-id and redirect uri as `YOUR_APP_SCHEME://YOUR_APP_HOST/YOUR_REDIRECT_PATH` (eg:`torusapp://org.torusresearch.torusdirectandroid/redirect`)

4. Trigger the login

5. Reach out to hello@tor.us to get your verifier spun up on the testnet today!

## Examples

Please refer to example for configuration

## Info

The following links help you create OAuth accounts with different login providers

- [Google](https://support.google.com/googleapi/answer/6158849)
- [Facebook](https://developers.facebook.com/docs/apps)
- [Reddit](https://github.com/reddit-archive/reddit/wiki/oauth2)
- [Twitch](https://dev.twitch.tv/docs/authentication/#registration)
- [Discord](https://discord.com/developers/docs/topics/oauth2)

For other verifiers,

- you'll need to create an [Auth0 account](https://auth0.com/)
- [create an application](https://auth0.com/docs/connections) for the login type you want
- Pass in the clientId, domain of the Auth0 application into the torus login request

## Best practices

- Please run the entire sdk calls in a new threadpool. Refer to example for basic configuration.

## FAQ

##

**Question:** Discord Login only works once in 30 min

**Answer:**
Torus Login requires a new token for every login attempt. Discord returns the same access token for 30 min unless it's revoked. Unfortunately, it needs to be revoked from the backend since it needs a client secret. Here's some sample code which does it

```js
const axios = require("axios").default;
const FormData = require("form-data");

const { DISCORD_CLIENT_SECRET, DISCORD_CLIENT_ID } = process.env;
const { token } = req.body;
const formData = new FormData();
formData.append("token", token);
await axios.post("https://discordapp.com/api/oauth2/token/revoke", formData, {
  headers: {
    ...formData.getHeaders(),
    Authorization: `Basic ${Buffer.from(`${DISCORD_CLIENT_ID}:${DISCORD_CLIENT_SECRET}`, "binary").toString("base64")}`,
  },
});
```

##

**Question:** How to initialise web3 with private key (returned after login) ?

**Answer:**
Use web3j

## Requirements

- Android - API level 24
- Java 8

