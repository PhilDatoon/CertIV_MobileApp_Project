/*
 * Copyright (C) 2011 The Android Open Source Project
 * Copyright (C) 2012 Zhenghong Wang
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
 * 
 * Search bar source code reference:
 * http://developer.android.com/training/search/setup.html
 */

package com.phildatoon.weather;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.phildatoon.at5.AlertDialogDisplay;
import com.phildatoon.at5.R;
import com.phildatoon.weather.WeatherInfo.ForecastInfo;
import com.phildatoon.weather.YahooWeather.SEARCH_MODE;

public class WeatherActivity extends Activity implements YahooWeatherInfoListener {
	
	private LinearLayout mWeatherInfosLayout;
	private ActionBar actionBar;
	
	// activity_weather.xml
	private LinearLayout mWeatherLayout;
	private ImageView mCurrentCondition_img;
	private TextView mCurrentLocation;
	private TextView mCurrentStateCountry;
	private TextView mCurrentTemp;
	private TextView mCurrentCondition;
	private TextView mCurrDayHigh;
	private TextView mCurrDayLow;
	private TextView mUpdate;

	private YahooWeather mYahooWeather = YahooWeather.getInstance();

    private ProgressDialog mProgressDialog;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        
		// adds back button on the action bar
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		handleIntent(getIntent());
		
        MyLog.init(getApplicationContext());
        
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();
        mProgressDialog.setMessage("Finding your location");
        
        // activity_weather.xml
        mWeatherLayout = (LinearLayout) findViewById(R.id.weatherLayout);
        mCurrentCondition_img = (ImageView) findViewById(R.id.img_currentCondition);
        mCurrentLocation = (TextView) findViewById(R.id.txt_location);
        mCurrentStateCountry = (TextView) findViewById(R.id.txt_state_country);
        mCurrentTemp = (TextView) findViewById(R.id.txt_currentTemp1);
        mCurrentCondition = (TextView) findViewById(R.id.txt_condition1);
        mCurrDayHigh = (TextView) findViewById(R.id.txt_highTemp1);
        mCurrDayLow = (TextView) findViewById(R.id.txt_lowTemp1);
        mWeatherInfosLayout = (LinearLayout) findViewById(R.id.weatherForecast);
        mUpdate = (TextView) findViewById(R.id.txt_lastBuild);
     
        mWeatherLayout.setVisibility(View.INVISIBLE);
      
	    // displays dialog box if device is not connected to the Internet and/or if GPS is disabled 
	    if (!NetworkUtils.isConnected(getApplicationContext())
	    		&& !(new UserLocationUtils().isGPSEnabled(getApplicationContext()))) {
	    	hideProgressDialog();
	    	new AlertDialogDisplay(this, "both");
        } else if (!NetworkUtils.isConnected(getApplicationContext())) {
	    	hideProgressDialog();
	    	new AlertDialogDisplay(this, "network");
        } else if (!(new UserLocationUtils().isGPSEnabled(getApplicationContext()))) {
	    	hideProgressDialog();
	    	new AlertDialogDisplay(this, "gps");
        }
	    
	    // searches for location initially
	    searchByGPS();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.weather, menu);
    
        // associates searchable configuration with the SearchView
	    SearchManager searchManager =
	            (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView =
	            (SearchView) menu.findItem(R.id.action_search_location).getActionView();
	    searchView.setSearchableInfo(
	            searchManager.getSearchableInfo(getComponentName()));

        return true;
    }
    
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		hideProgressDialog();
		mProgressDialog = null;
		super.onDestroy();
	}

	@SuppressLint({ "SimpleDateFormat", "DefaultLocale" })
	@Override
	public void gotWeatherInfo(WeatherInfo weatherInfo) {
		// TODO Auto-generated method stub
		hideProgressDialog();
        
		if (weatherInfo != null) {
        	setNormalLayout();
        	
        	if (mYahooWeather.getSearchMode() == SEARCH_MODE.GPS) {
        		// stops searching through GPS once location has been found
        		mYahooWeather.setSearchMode(SEARCH_MODE.PLACE_NAME);
        	}
        	
        	mWeatherInfosLayout.removeAllViews();
        	
        	final ForecastInfo currentForecastInfo = weatherInfo.getForecastInfoList().get(0);
        	
        	mCurrentCondition_img.setImageBitmap(weatherInfo.getCurrentConditionIcon());
        	mCurrentLocation.setText(weatherInfo.getLocationCity());
            mCurrentStateCountry.setText(((!weatherInfo.getLocationRegion().isEmpty() ) ?
            		weatherInfo.getLocationRegion() + ", " : "") + weatherInfo.getLocationCountry());
            mCurrentTemp.setText(weatherInfo.getCurrentTempC() + "ºC");
            mCurrentCondition.setText(weatherInfo.getCurrentText());
            mCurrDayHigh.setText("H: " + currentForecastInfo.getForecastTempHighC() + "º");
            mCurrDayLow.setText("L: " + currentForecastInfo.getForecastTempLowC() + "º");
        	
			if (weatherInfo.getCurrentConditionIcon() != null) {
	        	mCurrentCondition_img.setImageBitmap(weatherInfo.getCurrentConditionIcon());
			}
			
			for (int i = 1; i < YahooWeather.FORECAST_INFO_MAX_SIZE; i++) {
				final LinearLayout forecastInfoLayout = (LinearLayout) 
						getLayoutInflater().inflate(R.layout.weather_forecastinfo, null);
				
				final TextView fcDay = (TextView) forecastInfoLayout.findViewById(R.id.txt_forecastDay);
				final ImageView fcCondition_img = (ImageView) forecastInfoLayout.findViewById(R.id.img_forecastCondition);
				final TextView fcHighTemp = (TextView) forecastInfoLayout.findViewById(R.id.txt_forecastHighTemp);
				final TextView fcLowTemp = (TextView) forecastInfoLayout.findViewById(R.id.txt_forecastLowTemp);
				final ForecastInfo forecastInfo = weatherInfo.getForecastInfoList().get(i);
				
				fcDay.setText(forecastInfo.getForecastDay());
				fcHighTemp.setText(String.format("%2dº", forecastInfo.getForecastTempHighC()));
				fcLowTemp.setText(String.format("%2dº", forecastInfo.getForecastTempLowC()));

				if (forecastInfo.getForecastConditionIcon() != null) {
					fcCondition_img.setImageBitmap(forecastInfo.getForecastConditionIcon());
				}
				
				mWeatherInfosLayout.addView(forecastInfoLayout);
			}
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy 'at' hh:mm a");
			Date date = new Date();
			
			mUpdate.setText("UPDATED " + dateFormat.format(date).toUpperCase());
        } else {
        	setNoResultLayout();
        }
	}

	private void setNormalLayout() {
		mWeatherLayout.setVisibility(View.VISIBLE);
		mWeatherInfosLayout.setVisibility(View.VISIBLE);
	}
	
	private void setNoResultLayout() {		
		Toast.makeText(getApplicationContext(), "Location not found.", Toast.LENGTH_SHORT).show();
	}
	
	private void searchByGPS() {
		mYahooWeather.setNeedDownloadIcons(true);
		mYahooWeather.setSearchMode(SEARCH_MODE.GPS);
		mYahooWeather.queryYahooWeatherByGPS(getApplicationContext(), this);
	}
	
	private void searchByPlaceName(String location) {
		mYahooWeather.setNeedDownloadIcons(true);
		mYahooWeather.setSearchMode(SEARCH_MODE.PLACE_NAME);
		mYahooWeather.queryYahooWeatherByPlaceName(getApplicationContext(), location, WeatherActivity.this);
	}
	
	private void showProgressDialog() {
      	if (mProgressDialog != null && mProgressDialog.isShowing()) {
      		mProgressDialog.cancel();
      	}
        mProgressDialog = new ProgressDialog(WeatherActivity.this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();
	}
	
	private void hideProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.cancel();
		}
	}
	
	@Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }
	
	private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            
            //use the query to search your data somehow
            Log.d("QUERY", query);
            searchByPlaceName(query);	
            showProgressDialog();
            mProgressDialog.setMessage("Searching for location");
        }
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
        case R.id.action_locate_gps:
        	// executes action if "Locate using GPS" menu item is selected
        	searchByGPS();
			showProgressDialog();
            mProgressDialog.setMessage("Finding your location");
            return true;
            
        case android.R.id.home:
        	// finishes the activity once user pressed back button on action bar
    		this.finish();
    		return true;
    		
        default:
            return super.onOptionsItemSelected(item);
		}
	}
}
