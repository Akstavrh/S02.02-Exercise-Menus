/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler{

    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;

    private TextView mErrorMessage;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        /*
         * Using findViewById, we get a reference to our TextView from xml. This allows us to
         * do things like set the text of the TextView.
         */

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_forecast);

        mErrorMessage =(TextView) findViewById(R.id.tv_error_message);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);


        mForecastAdapter = new ForecastAdapter(this);

        mRecyclerView.setAdapter(mForecastAdapter);

        mLoadingIndicator =(ProgressBar) findViewById(R.id.pb_loading_indicator);

        /* Once all of our views are setup, we can load the weather data. */
        loadWeatherData();
    }

    /**
     * This method will get the user's preferred location for weather, and then tell some
     * background method to get the weather data in the background.
     */
    private void loadWeatherData() {
        String location = SunshinePreferences.getPreferredWeatherLocation(this);
        new FetchWeatherTask().execute(location);
    }

    @Override
    public void onItemClick(String s) {
        Context context = this;
        Toast.makeText(context, s,Toast.LENGTH_SHORT)
                .show();
        Intent detailIntent = new Intent(context, DetailActivity.class);
        detailIntent.putExtra(Intent.EXTRA_TEXT, s);
        startActivity(detailIntent);

    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {

            /* If there's no zip code, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            String location = params[0];
            URL weatherRequestUrl = NetworkUtils.buildUrl(location);

            try {
                String jsonWeatherResponse = NetworkUtils
                        .getResponseFromHttpUrl(weatherRequestUrl);

                String[] simpleJsonWeatherData = OpenWeatherJsonUtils
                        .getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);

                return simpleJsonWeatherData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] weatherData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (weatherData != null) {
                mForecastAdapter.saveWeatherData(weatherData);
                showForecastData();
            } else {
                showErrorMessage();
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_refresh:
                mForecastAdapter.saveWeatherData(null);
                loadWeatherData();
            case R.id.open_map:
                showMap();
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void showErrorMessage(){
        mErrorMessage.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void showForecastData(){
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    public void showMap() {
        Uri location;
        String address = "1600 Amphitheatre Parkway, Mountain View, CA 94043";
        location = Uri.parse("geo:0,0?" + address);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(location);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    // OK (2) Create a menu resource in res/menu/ called forecast.xml
    // OK (3) Add one item to the menu with an ID of action_refresh
    // OK (4) Set the title of the menu item to "Refresh" using strings.xml

    // OK (5) Override onCreateOptionsMenu to inflate the menu for this Activity
    // OK (6) Return true to display the menu

    // OK (7) Override onOptionsItemSelected to handle clicks on the refresh button
}