package com.example.android.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private TextView mWeatherForDayTextView;
    private String mForecast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mWeatherForDayTextView = (TextView) findViewById(R.id.tv_weather_details);

        Intent intent = getIntent();

        if (intent != null){
            if (intent.hasExtra(Intent.EXTRA_TEXT)){
                mForecast= intent.getStringExtra(Intent.EXTRA_TEXT);
                mWeatherForDayTextView.setText(mForecast);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.detail_forecast_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.share_forecast:
                shareData();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void shareData(){
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mForecast)
                .getIntent();
        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(shareIntent);
        }
    }
}
