package org.torusresearch.customauth.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.bitcoinj.core.Base58;
import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.utils.TweetNaclFast;
import org.torusresearch.customauth.CustomAuth;
import org.torusresearch.customauth.types.AggregateLoginParams;
import org.torusresearch.customauth.types.AggregateVerifierType;
import org.torusresearch.customauth.types.Auth0ClientOptions;
import org.torusresearch.customauth.types.CustomAuthArgs;
import org.torusresearch.customauth.types.LoginType;
import org.torusresearch.customauth.types.NoAllowedBrowserFoundException;
import org.torusresearch.customauth.types.SubVerifierDetails;
import org.torusresearch.customauth.types.TorusAggregateLoginResponse;
import org.torusresearch.customauth.types.TorusLoginResponse;
import org.torusresearch.customauth.types.UserCancelledException;
import org.torusresearch.customauth.utils.Helpers;
import org.torusresearch.fetchnodedetails.types.NodeDetails;
import org.torusresearch.fetchnodedetails.types.TorusNetwork;
import org.torusresearch.torusutils.types.TorusPublicKey;
import org.torusresearch.torusutils.types.VerifierArgs;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private final HashMap<String, LoginVerifier> verifierMap = new HashMap<String, LoginVerifier>() {
        {
            put("google", new LoginVerifier("Google", LoginType.GOOGLE, "221898609709-obfn3p63741l5333093430j3qeiinaa8.apps.googleusercontent.com", "google-lrc"));
            put("facebook", new LoginVerifier("Facebook", LoginType.FACEBOOK, "617201755556395", "facebook-lrc"));
            put("twitch", new LoginVerifier("Twitch", LoginType.TWITCH, "f5and8beke76mzutmics0zu4gw10dj", "twitch-lrc"));
            put("discord", new LoginVerifier("Discord", LoginType.DISCORD, "682533837464666198", "discord-lrc"));
            String domain = "torus-test.auth0.com";
            put("email_password", new LoginVerifier("Email Password", LoginType.EMAIL_PASSWORD, "sqKRBVSdwa4WLkaq419U7Bamlh5vK1H7", "torus-auth0-email-password", domain));
            put("apple", new LoginVerifier("Apple", LoginType.APPLE, "m1Q0gvDfOyZsJCZ3cucSQEe9XMvl9d9L", "torus-auth0-apple-lrc", domain));
            put("github", new LoginVerifier("Github", LoginType.GITHUB, "PC2a4tfNRvXbT48t89J5am0oFM21Nxff", "torus-auth0-github-lrc", domain));
            put("linkedin", new LoginVerifier("LinkedIn", LoginType.LINKEDIN, "59YxSgx79Vl3Wi7tQUBqQTRTxWroTuoc", "torus-auth0-linkedin-lrc", domain));
            put("twitter", new LoginVerifier("Twitter", LoginType.TWITTER, "A7H8kkcmyFRlusJQ9dZiqBLraG2yWIsO", "torus-auth0-twitter-lrc", domain));
            put("line", new LoginVerifier("Line", LoginType.APPLE, "WN8bOmXKNRH1Gs8k475glfBP5gDZr9H1", "torus-auth0-line-lrc", domain));
            put("hosted_email_passwordless", new LoginVerifier("Hosted Email Passwordless", LoginType.JWT, "P7PJuBCXIHP41lcyty0NEb7Lgf7Zme8Q", "torus-auth0-passwordless", domain, "name", false));
            put("hosted_sms_passwordless", new LoginVerifier("Hosted SMS Passwordless", LoginType.JWT, "nSYBFalV2b1MSg5b2raWqHl63tfH3KQa", "torus-auth0-sms-passwordless", domain, "name", false));
//            put("torus_passwordless", new LoginVerifier("Torus Passwordless", LoginType.JWT, "KG7zk89X3QgttSyX9NJ4fGEyFNhOcJTw", "tkey-auth0-email-passwordless-cyan", "auth.openlogin.com", "name", false));
        }
    };

    private final String[] allowedBrowsers = new String[]{
            "com.android.chrome", // Chrome stable
            "com.google.android.apps.chrome", // Chrome system
            "com.android.chrome.beta", // Chrome beta
    };

    private CustomAuth torusSdk;
    private LoginVerifier selectedLoginVerifier;
    private BigInteger privKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Option 1. Deep links if your OAuth provider supports it
        // DirectSdkArgs args = new DirectSdkArgs("torusapp://org.torusresearch.customauthandroid/redirect", TorusNetwork.TESTNET);

        // Option 2. Host redirect.html at your domain and proxy redirect to your app
        // clientId is mandatory field.
        CustomAuthArgs args = new CustomAuthArgs("https://scripts.toruswallet.io/redirect.html", TorusNetwork.SAPPHIRE_DEVNET, "torusapp://org.torusresearch.customauthandroid/redirect",
                "BG4pe3aBso5SjVbpotFQGnXVHgxhgOxnqnNBKyjfEJ3izFvIVWUaMIzoCrAfYag8O6t6a6AOvdLcS4JR2sQMjR4");
        args.setEnableOneKey(true);

        // Initialize CustomAuth
        this.torusSdk = new CustomAuth(args, this);
        Spinner spinner = findViewById(R.id.verifierList);
        List<LoginVerifier> loginVerifierList = new ArrayList<>(verifierMap.values());
        ArrayAdapter<LoginVerifier> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, loginVerifierList);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }


    public void launch(View view) {
        singleLoginTest();
        // aggregateLoginTest();
    }

    public TweetNaclFast.Signature.KeyPair getEd25199Key(String privateKey) {
        byte[] decodedBytes = TweetNaclFast.hexDecode(privateKey);
        TweetNaclFast.Signature.KeyPair ed25519KeyPair = TweetNaclFast.Signature.keyPair_fromSeed(decodedBytes);
        return ed25519KeyPair;
    }

    public void createSolanaAccount(View view) {
        TextView textView = findViewById(R.id.output);

        if (this.privKey != null) {
            textView.setText("Please login first to generate solana ed25519 key pair");
            return;
        }
        TweetNaclFast.Signature.KeyPair ed25519KeyPair = this.getEd25199Key(this.privKey.toString(16));
        Account SolanaAccount = new Account(ed25519KeyPair.getSecretKey());
        String pubKey = SolanaAccount.getPublicKey().toBase58();
        String secretKey = Base58.encode(SolanaAccount.getSecretKey());
        String accountInfo = String.format("Solana account secret key is %s and public Key %s", secretKey, pubKey);
        textView.setText(accountInfo);
    }

    public void getTorusKey(View view) throws ExecutionException, InterruptedException {
        String verifier = "google-lrc";
        String verifierId = "hello@tor.us";
        HashMap<String, Object> verifierParamsHashMap = new HashMap<>();
        verifierParamsHashMap.put("verifier_id", verifierId);
        String idToken = "";
        NodeDetails nodeDetails = torusSdk.nodeDetailManager.getNodeDetails(verifier, verifierId).get();
        TorusPublicKey publicKey = torusSdk.torusUtils.getPublicAddress(nodeDetails.getTorusNodeEndpoints(), nodeDetails.getTorusNodePub(), new VerifierArgs(verifier, verifierId)).get();
        Log.d("public address", publicKey.getFinalKeyData().evmAddress);
        // torusSdk.getTorusKey(verifier, verifierId, verifierParamsHashMap, idToken);
    }

    private void renderError(Throwable error) {
        Log.e("result:error", "error", error);
        Throwable reason = Helpers.unwrapCompletionException(error);
        TextView textView = findViewById(R.id.output);
        if (reason instanceof UserCancelledException || reason instanceof NoAllowedBrowserFoundException)
            textView.setText(error.getMessage());
        else
            textView.setText("Something went wrong: " + error.getMessage());
    }

    @SuppressLint("SetTextI18n")
    public void singleLoginTest() {
        Log.d("result:selecteditem", this.selectedLoginVerifier.toString());
        Auth0ClientOptions.Auth0ClientOptionsBuilder builder = null;
        if (this.selectedLoginVerifier.getDomain() != null) {
            builder = new Auth0ClientOptions.Auth0ClientOptionsBuilder(this.selectedLoginVerifier.getDomain());
            builder.setVerifierIdField(this.selectedLoginVerifier.getVerifierIdField());
            builder.setVerifierIdCaseSensitive(this.selectedLoginVerifier.isVerfierIdCaseSensitive());
        }
        CompletableFuture<TorusLoginResponse> torusLoginResponseCf;
        if (builder == null) {
            torusLoginResponseCf = this.torusSdk.triggerLogin(new SubVerifierDetails(this.selectedLoginVerifier.getTypeOfLogin(),
                    this.selectedLoginVerifier.getVerifier(),
                    this.selectedLoginVerifier.getClientId())
                    .setPreferCustomTabs(true)
                    .setAllowedBrowsers(allowedBrowsers));
        } else {
            torusLoginResponseCf = this.torusSdk.triggerLogin(new SubVerifierDetails(this.selectedLoginVerifier.getTypeOfLogin(),
                    this.selectedLoginVerifier.getVerifier(),
                    this.selectedLoginVerifier.getClientId(), builder.build())
                    .setPreferCustomTabs(true)
                    .setAllowedBrowsers(allowedBrowsers));
        }

        torusLoginResponseCf.whenComplete((torusLoginResponse, error) -> {
            if (error != null) {
                renderError(error);
            } else {
                String publicAddress = torusLoginResponse.getPublicAddress();
                this.privKey = torusLoginResponse.getPrivateKey();
                Log.d(MainActivity.class.getSimpleName(), publicAddress);
                Log.d(MainActivity.class.getSimpleName(), String.valueOf(this.privKey));
                ((TextView) findViewById(R.id.output)).setText("Public Key: " + publicAddress);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void aggregateLoginTest() {
        CompletableFuture<TorusAggregateLoginResponse> torusLoginResponseCf = this.torusSdk.triggerAggregateLogin(new AggregateLoginParams(AggregateVerifierType.SINGLE_VERIFIER_ID,
                "chai-google-aggregate-test", new SubVerifierDetails[]{
                new SubVerifierDetails(LoginType.GOOGLE, "google-chai", "884454361223-nnlp6vtt0me9jdsm2ptg4d1dh8i0tu74.apps.googleusercontent.com")
        }));

        torusLoginResponseCf.whenComplete((torusAggregateLoginResponse, error) -> {
            if (error != null) {
                renderError(error);
            } else {
                String json = torusAggregateLoginResponse.getPublicAddress();
                Log.d(MainActivity.class.getSimpleName(), json);
                ((TextView) findViewById(R.id.output)).setText(json);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        this.selectedLoginVerifier = (LoginVerifier) adapterView.getSelectedItem();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
