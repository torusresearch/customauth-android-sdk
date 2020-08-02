package org.torusresearch.torusdirectandroid;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.torusresearch.torusdirect.TorusDirectSdk;
import org.torusresearch.torusdirect.types.Auth0ClientOptions;
import org.torusresearch.torusdirect.types.DirectSdkArgs;
import org.torusresearch.torusdirect.types.LoginType;
import org.torusresearch.torusdirect.types.SubVerifierDetails;
import org.torusresearch.torusdirect.types.TorusLoginResponse;
import org.torusresearch.torusdirect.types.TorusNetwork;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @SuppressLint("SetTextI18n")
    public void launch(View view) {
        DirectSdkArgs args = new DirectSdkArgs("torusapp://org.torusresearch.torusdirectandroid/redirect", TorusNetwork.TESTNET, "0x4023d2a0D330bF11426B12C6144Cfb96B7fa6183");
        TorusDirectSdk sdk = new TorusDirectSdk(args, this);
        CompletableFuture<TorusLoginResponse> torusLoginResponseCompletableFuture = sdk.triggerLogin(new SubVerifierDetails(LoginType.GOOGLE,
                "google-lrc",
                "221898609709-obfn3p63741l5333093430j3qeiinaa8.apps.googleusercontent.com",
                new Auth0ClientOptions.Auth0ClientOptionsBuilder("").build()));
        Executors.newFixedThreadPool(10).submit(() -> {
            try {
                TorusLoginResponse torusLoginResponse = torusLoginResponseCompletableFuture.get();
                Log.d(MainActivity.class.getSimpleName(), "Private Key:: " + torusLoginResponse.getPrivateKey());
                Log.d(MainActivity.class.getSimpleName(), "Public Address:: " + torusLoginResponse.getPublicAddress());
                runOnUiThread(() -> ((TextView)findViewById(R.id.output)).setText("Private Key:: " + torusLoginResponse.getPrivateKey() + "\n" + "Public Address:: " + torusLoginResponse.getPublicAddress()));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                runOnUiThread(() -> ((TextView)findViewById(R.id.output)).setText("Something went wrong."));
            }
        });
    }
}
