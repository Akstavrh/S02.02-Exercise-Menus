package com.example.android.sunshine;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by francescaeeros on 02/01/18.
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private String[] mWeatherData;
    private final ForecastAdapterOnClickHandler mClickHandler;

    public ForecastAdapter(ForecastAdapterOnClickHandler clickHandler) {
        this.mClickHandler = clickHandler;
    }

    public interface ForecastAdapterOnClickHandler{
        void onItemClick(String s);
    }



    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder
                                                    implements View.OnClickListener{

        public final TextView mWeatherTextView;

        public ForecastAdapterViewHolder(View itemView) {
            super(itemView);
            mWeatherTextView = (TextView) itemView.findViewById(R.id.tv_weather_data);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String weatherForTheDay = mWeatherData[adapterPosition];
            mClickHandler.onItemClick(weatherForTheDay);
        }
    }

    public void saveWeatherData(String[] weatherData){
        mWeatherData = weatherData;
        notifyDataSetChanged();
    }

    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.forecast_list_item, parent, false);

        ForecastAdapterViewHolder vh = new ForecastAdapterViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder holder, int position) {

        holder.mWeatherTextView.setText(mWeatherData[position]);

    }

    @Override
    public int getItemCount() {
        if (mWeatherData == null){
            return 0;
        } else{
            return mWeatherData.length;
        }
    }

}
