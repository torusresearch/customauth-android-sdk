package org.torusresearch.torusdirect.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import org.torusresearch.torusdirect.R;
import org.torusresearch.torusdirect.interfaces.ILoginHandler;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class StartUpActivity extends AppCompatActivity {
    public static final String URL = "URL";
    public static AtomicReference<ILoginHandler> loginHandler = new AtomicReference<>();
    private AtomicBoolean isLoginStep = new AtomicBoolean();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        isLoginStep.set(true);
        CustomTabsIntent intent = new CustomTabsIntent.Builder().build();
        String data = getIntent().getStringExtra(URL);
        if (data == null) {
            Log.d("init:torus", "getStringExtra(URL) is NULL!!");
            data = getIntent().getDataString();
        }
        intent.launchUrl(this, Uri.parse(data));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isLoginStep.get()) {
            finish();
        } else {
            isLoginStep.set(false);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.getData() != null) {
            Log.d("result:torus", Objects.requireNonNull(intent.getData()).toString());
            if (loginHandler != null && loginHandler.get() != null) {
                loginHandler.get().setResponse(intent.getData().toString());
                loginHandler.set(null);
                finish();
            }
        }
    }
}
