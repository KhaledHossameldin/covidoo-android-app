package com.example.covidoo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.RenderProcessGoneDetail;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    ConstraintLayout constraintLayout;
    SwipeRefreshLayout refreshLayout;
    LoaderManager loaderManager;
    ProgressBar progressBar;
    ImageView noInternet;
    TextView countryTextView;
    TextView dateTextView;
    TextView activeCases;
    TextView confirmedCases;
    TextView deathsCases;
    TextView recoveredCases;
    TextView lastData;
    TextView newCasesTextView;
    TextView newDeathsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.app_name) + "</font>"));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.covidoo_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences preferences = getSharedPreferences("CovidData", Context.MODE_PRIVATE);
        boolean[] choices = {
                preferences.getBoolean("NewCasesShow", true),
                preferences.getBoolean("ActiveCasesShow", true),
                preferences.getBoolean("ConfirmedCasesShow", true),
                preferences.getBoolean("NewDeathsCasesShow", true),
                preferences.getBoolean("DeathsCasesShow", true),
                preferences.getBoolean("RecoveredCasesShow", true)
        };
        String[] items = getResources().getStringArray(R.array.choices);


        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Choose which to show")
                .setMultiChoiceItems(items, choices, (dialog, which, isChecked) -> {})
                .setPositiveButton("Confirm", (dialog, which) -> {
                    if (progressBar.getVisibility() != View.VISIBLE) {
                        preferences.edit()
                                .putBoolean("NewCasesShow", choices[0])
                                .putBoolean("ActiveCasesShow", choices[1])
                                .putBoolean("ConfirmedCasesShow", choices[2])
                                .putBoolean("NewDeathsCasesShow", choices[3])
                                .putBoolean("DeathsCasesShow", choices[4])
                                .putBoolean("RecoveredCasesShow", choices[5])
                                .apply();
                        HandleCasesTextViews();
                    } else {
                        Snackbar.make(constraintLayout, "Can't do this now", Snackbar.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("Cancel", null).show();

        return super.onOptionsItemSelected(item);
    }

    private void init() {
        constraintLayout = findViewById(R.id.ConstraintLayout);
        refreshLayout = findViewById(R.id.RefreshLayout);
        progressBar = findViewById(R.id.DataProgressBar);
        noInternet = findViewById(R.id.NoInternetImageView);
        countryTextView = findViewById(R.id.CountryTextView);
        dateTextView = findViewById(R.id.DateTextView);
        activeCases = findViewById(R.id.ActiveCasesTextView);
        confirmedCases = findViewById(R.id.ConfirmedCasesTextView);
        deathsCases = findViewById(R.id.DeathsTextView);
        recoveredCases = findViewById(R.id.RecoveredTextView);
        newCasesTextView = findViewById(R.id.NewCasesTextView);
        newDeathsTextView = findViewById(R.id.NewDeathsTextView);
        lastData = findViewById(R.id.LastDataTextView);
        loaderManager = getSupportLoaderManager();
        if (loaderManager.getLoader(1) == null) {
            loaderManager.initLoader(1, null, this);
        }

        refreshLayout.setOnRefreshListener(() -> {
            constraintLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            loaderManager.initLoader(1, null, MainActivity.this);
        });

        HandleCasesTextViews();
    }

    private void HandleCasesTextViews() {
        SharedPreferences preferences = getSharedPreferences("CovidData", Context.MODE_PRIVATE);


        if (preferences.getBoolean("NewCasesShow", true) && preferences.getBoolean("ActiveCasesShow", true)) {
            newCasesTextView.setVisibility(View.VISIBLE);
        } else {
            newCasesTextView.setVisibility(View.GONE);
        }
        if (preferences.getBoolean("ActiveCasesShow", true)) {
            activeCases.setVisibility(View.VISIBLE);
        } else {
            activeCases.setVisibility(View.GONE);
        }

        if (preferences.getBoolean("ConfirmedCasesShow", true)) {
            confirmedCases.setVisibility(View.VISIBLE);
        } else {
            confirmedCases.setVisibility(View.GONE);
        }

        if (preferences.getBoolean("NewDeathsCasesShow", true) && preferences.getBoolean("DeathsCasesShow", true)) {
            newDeathsTextView.setVisibility(View.VISIBLE);
        } else {
            newDeathsTextView.setVisibility(View.GONE);
        }

        if (preferences.getBoolean("DeathsCasesShow", true)) {
            deathsCases.setVisibility(View.VISIBLE);
        } else {
            deathsCases.setVisibility(View.GONE);
        }

        if (preferences.getBoolean("RecoveredCasesShow", true)) {
            recoveredCases.setVisibility(View.VISIBLE);
        } else {
            recoveredCases.setVisibility(View.GONE);
        }
    }

    public void ShareClick(View view) {
        String[] shareItems = new String[6];
        shareItems[0] = "New Cases: " + newCasesTextView.getText().toString().substring(1);
        shareItems[1] = activeCases.getText().toString();
        shareItems[2] = confirmedCases.getText().toString();
        shareItems[3] = "New Deaths: " + newDeathsTextView.getText().toString().substring(1);
        shareItems[4] = deathsCases.getText().toString();
        shareItems[5] = recoveredCases.getText().toString();

        boolean[] choices = new boolean[6];
        String[] items = getResources().getStringArray(R.array.choices);
        Arrays.fill(choices, true);


        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Choose what to share")
                .setMultiChoiceItems(items, choices, (dialogInterface, i, b) -> {})
                .setPositiveButton("Share", (dialogInterface, i) -> {
                    String s = "";
                    for (int index=0; index<items.length; index++) {
                        if (choices[index]) {
                            s += shareItems[index] + "\n";
                        }
                    }
                    if (!s.matches("")) {
                        s = s.substring(0, s.length() - 1);
                        s += "\n\nShared via Covidoo\nCovidoo.vitetime.co";
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TEXT, countryTextView.getText().toString() + "\n" + s);
                        intent.setType("text/plain");
                        startActivity(intent);
                    } else {
                        Snackbar.make(constraintLayout, "Nothing was chosen", Snackbar.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("Cancel", null).show();
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        return new DataDownloader(getApplicationContext());
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        progressBar.setVisibility(View.GONE);
        constraintLayout.setVisibility(View.VISIBLE);

        int confirmed = -1;
        String newCases = null;
        int recovered = -1;
        int deaths = -1;
        String newDeaths = null;
        int active = -1;
        String date = null;

        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                JSONObject object = jsonArray.getJSONObject(jsonArray.length() - 1);
                date = object.getString("day");

                JSONObject casesObject = object.getJSONObject("cases");
                active = casesObject.getInt("active");
                newCases = casesObject.getString("new");
                recovered = casesObject.getInt("recovered");
                confirmed = casesObject.getInt("total");

                JSONObject deathsObject = object.getJSONObject("deaths");
                deaths = deathsObject.getInt("total");
                newDeaths = deathsObject.getString("new");

                SharedPreferences preferences = getSharedPreferences("CovidData", Context.MODE_PRIVATE);

                int savedActive = preferences.getInt("ActiveCases", 0);
                if (savedActive != active) {
                    preferences.edit()
                            .putInt("ActiveCases", active)
                            .putInt("ConfirmedCases", confirmed)
                            .putString("NewCases", newCases)
                            .putInt("RecoveredCases", recovered)
                            .putInt("DeathCases", deaths)
                            .putString("newDeaths", newDeaths)
                            .putString("Date", date).apply();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            Snackbar.make(constraintLayout, "No Internet Connection", Snackbar.LENGTH_SHORT).show();
            noInternet.setVisibility(View.VISIBLE);

            SharedPreferences preferences = getSharedPreferences("CovidData", Context.MODE_PRIVATE);

            active = preferences.getInt("ActiveCases", 0);
            confirmed = preferences.getInt("ConfirmedCases", 0);
            newCases = preferences.getString("NewCases", "+0");
            recovered = preferences.getInt("RecoveredCases", 0);
            deaths = preferences.getInt("DeathCases", 0);
            newDeaths = preferences.getString("newDeaths", "+0");
            date = preferences.getString("Date", "No Date");

            lastData.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> lastData.animate().alpha(0), 3000);
        }

        activeCases.setText(String.format("%s %d", getString(R.string.active_cases), active));
        confirmedCases.setText(String.format("%s %d", getString(R.string.confirmed_cases), confirmed));
        recoveredCases.setText(String.format("%s %d", getString(R.string.recovered), recovered));
        deathsCases.setText(String.format("%s %d", getString(R.string.deaths), deaths));
        newCasesTextView.setText(newCases);
        if (newCases.startsWith("+")) {
            newCasesTextView.setTextColor(Color.RED);
        } else {
            newCasesTextView.setTextColor(Color.GREEN);
        }
        newDeathsTextView.setText(newDeaths);
        if (newDeaths.startsWith("+")) {
            newDeathsTextView.setTextColor(Color.RED);
        } else {
            newDeathsTextView.setTextColor(Color.GREEN);
        }
        dateTextView.setText(date);
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}