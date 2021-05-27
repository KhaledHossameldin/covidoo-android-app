package com.example.covidoo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import java.util.Objects;

public class HappyNewYearActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_happy_new_year);
        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.app_name) + "</font>"));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void GoToMain(View view) {
        Intent intent = new Intent(HappyNewYearActivity.this, MainActivity.class);
        SharedPreferences sharedPreferences = getSharedPreferences("CovidData", Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("HappyNewYearDisplayed", true).apply();
        startActivity(intent);
    }
}