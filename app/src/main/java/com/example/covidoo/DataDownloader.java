package com.example.covidoo;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.io.IOException;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DataDownloader extends AsyncTaskLoader<String> {
    public DataDownloader(@NonNull Context context) {
        super(context);
    }

    @Nullable
    @Override
    public String loadInBackground() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://covid-193.p.rapidapi.com/statistics?country=Egypt")
                .get()
                .addHeader("x-rapidapi-key", "f64ccb4363msh0fb6f48ec490b3bp1ea3a3jsnced2c3399de4")
                .addHeader("x-rapidapi-host", "covid-193.p.rapidapi.com")
                .build();

        try {
            Response response = client.newCall(request).execute();
            return Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
