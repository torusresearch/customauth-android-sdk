package org.torusresearch.torusdirect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", getIntent().getData().toString());
        setResult(200, returnIntent);
        finish();
    }
}
