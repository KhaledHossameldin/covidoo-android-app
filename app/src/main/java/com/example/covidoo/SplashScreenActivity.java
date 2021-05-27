package com.example.covidoo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;

import java.util.Objects;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.app_name) + "</font>"));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SharedPreferences sharedPreferences = getSharedPreferences("CovidData", Context.MODE_PRIVATE);
        boolean HappyNewYearDisplayed = sharedPreferences.getBoolean("HappyNewYearDisplayed", false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = null;
                if (HappyNewYearDisplayed) {
                    intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(SplashScreenActivity.this, HappyNewYearActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

    public void Click(View view) {
        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        startActivity(intent);
    }
}