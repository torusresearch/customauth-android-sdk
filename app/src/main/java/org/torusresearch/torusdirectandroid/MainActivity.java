package org.torusresearch.torusdirectandroid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsIntent;

import org.torusresearch.torusdirect.TorusDirectSdk;
import org.torusresearch.torusdirect.types.DirectSdkArgs;

public class MainActivity extends AppCompatActivity {

    private static final Uri LAUNCH_URI =
            Uri.parse("https://google.com");
    private String providerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        providerName = CustomTabsClient.getPackageName(this, null);
    }

    public void launch(View view) {
//        CustomTabsIntent intent = new CustomTabsIntent.Builder().build();
//        intent.intent.setPackage(providerName);
//        intent.intent.setData(LAUNCH_URI);
//        Log.d("result:torus", "Starting activity");
//        startActivityForResult(intent.intent, 200);
//        Log.d("result:torus", "Started activity");
        DirectSdkArgs args = new DirectSdkArgs("https://app.tor.us/redirect");
        args.setGoogleClientId("876733105116-i0hj3s53qiio5k95prpfmj0hp0gmgtor.apps.googleusercontent.com");
        TorusDirectSdk sdk = new TorusDirectSdk(this, args);
        Intent intent = sdk.triggerLogin("google", "google");
        startActivityForResult(intent, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("result:torus", data.getStringExtra("result"));
    }
}
