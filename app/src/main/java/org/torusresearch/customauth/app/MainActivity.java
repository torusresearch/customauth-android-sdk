package org.torusresearch.customauth.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

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
import org.torusresearch.customauth.types.TorusKey;
import org.torusresearch.customauth.types.TorusLoginResponse;
import org.torusresearch.customauth.types.TorusNetwork;
import org.torusresearch.customauth.types.UserCancelledException;
import org.torusresearch.customauth.utils.Helpers;
import org.torusresearch.fetchnodedetails.types.NodeDetails;
import org.torusresearch.torusutils.types.TorusPublicKey;
import org.torusresearch.torusutils.types.VerifierArgs;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import java8.util.concurrent.CompletableFuture;
import java8.util.function.BiConsumer;

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
        }
    };

    private final String[] allowedBrowsers = new String[]{
            "com.android.chrome", // Chrome stable
            "com.google.android.apps.chrome", // Chrome system
            "com.android.chrome.beta", // Chrome beta
    };

    private CustomAuth torusSdk;
    private GoogleSignInClient googleSignIn;
    private SignInButton buttonGoogleLogin;
    private TextView output;
    private static final int RC_GOOGLE_SIGN_IN = 1;
    private LoginVerifier selectedLoginVerifier;
    private String privKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Option 1. Deep links if your OAuth provider supports it
        // DirectSdkArgs args = new DirectSdkArgs("torusapp://org.torusresearch.customauthandroid/redirect", TorusNetwork.TESTNET);

        // Option 2. Host redirect.html at your domain and proxy redirect to your app
        CustomAuthArgs args = new CustomAuthArgs("https://scripts.toruswallet.io/redirect.html", TorusNetwork.TESTNET, "torusapp://org.torusresearch.customauthandroid/redirect");

        // Initialize CustomAuth
        this.torusSdk = new CustomAuth(args, this);
        Spinner spinner = findViewById(R.id.verifierList);
        output = findViewById(R.id.output);
        output.setMovementMethod(new ScrollingMovementMethod());
        List<LoginVerifier> loginVerifierList = new ArrayList<>(verifierMap.values());
        ArrayAdapter<LoginVerifier> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, loginVerifierList);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        googleSignIn = GoogleSignIn.getClient(
                this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestId()
                        .requestEmail()
                        .requestProfile()
                        .requestIdToken(getString(R.string.torus_native_google_client_id))
                        .build()
        );

        buttonGoogleLogin = findViewById(R.id.button_native_google_login);
        buttonGoogleLogin.setOnClickListener(v -> {
            googleSignIn.revokeAccess().continueWith(res -> googleSignIn.signOut())
            .continueWith(res -> {
                startActivityForResult(googleSignIn.getSignInIntent(), RC_GOOGLE_SIGN_IN);
                return null;
            });


//            Intent signInIntent = googleSignIn.getSignInIntent();
//            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
        });
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

        if (this.privKey.isEmpty()) {
            output.setText("Please login first to generate solana ed25519 key pair");
            return;
        }
        TweetNaclFast.Signature.KeyPair ed25519KeyPair = this.getEd25199Key(this.privKey);
        Account SolanaAccount = new Account(ed25519KeyPair.getSecretKey());
        String pubKey = SolanaAccount.getPublicKey().toBase58();
        String secretKey = Base58.encode(SolanaAccount.getSecretKey());
        String accountInfo = String.format("Solana account secret key is %s and public Key %s", secretKey, pubKey);
        output.setText(accountInfo);
    }

    public void getTorusKey(View view) throws ExecutionException, InterruptedException {
        String verifier = "google-lrc";
        String verifierId = "hello@tor.us";
        HashMap<String, Object> verifierParamsHashMap = new HashMap<>();
        verifierParamsHashMap.put("verifier_id", verifierId);
        String idToken = "";
        NodeDetails nodeDetails = torusSdk.nodeDetailManager.getNodeDetails().get();
        TorusPublicKey publicKey = torusSdk.torusUtils.getPublicAddress(nodeDetails.getTorusNodeEndpoints(), nodeDetails.getTorusNodePub(), new VerifierArgs(verifier, verifierId)).get();
        Log.d("public address", publicKey.getAddress());
        // torusSdk.getTorusKey(verifier, verifierId, verifierParamsHashMap, idToken);
    }

    private void renderError(Throwable error) {
        Log.e("result:error", "error", error);
        Throwable reason = Helpers.unwrapCompletionException(error);

        if (reason instanceof UserCancelledException || reason instanceof NoAllowedBrowserFoundException)
            output.setText(error.getMessage());
        else
            output.setText("Something went wrong: " + error.getMessage());
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
                output.setText(publicAddress);
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
                output.setText(json);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task =  GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount googleAccount = task.getResult(ApiException.class);
                Log.d("lol email", googleAccount.getEmail());
                Log.d("lol tokem", googleAccount.getIdToken());

                HashMap<String, Object> verifierHashMap = new HashMap<>();
                verifierHashMap.put("verifier_id", googleAccount.getEmail());

                torusSdk.getTorusKey(
                        getString(R.string.torus_native_google_verifier_id),
                        googleAccount.getEmail(),
                        verifierHashMap,
                        googleAccount.getIdToken()
                ).whenComplete((res, error) ->{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (error != null) {
                                Log.d("lol", error.getMessage().toString());
                                output.setText("Error :" + error.getMessage());
                            } else {
                                output.setText(res.getPublicAddress());
                            }
                        }
                    });
                });

            } catch (ApiException e) {
                Log.d("lol", e.toString());
                output.setText("Error :" + e.getMessage());
            }
        }
    }


}
