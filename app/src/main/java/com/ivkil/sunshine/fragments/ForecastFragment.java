package com.ivkil.sunshine.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ivkil.sunshine.R;
import com.ivkil.sunshine.activities.DetailActivity;
import com.ivkil.sunshine.application.ApplicationController;
import com.ivkil.sunshine.models.WeatherData;
import com.ivkil.sunshine.networking.GsonRequest;

import java.util.ArrayList;
import java.util.Arrays;

public class ForecastFragment extends Fragment {

    private final String TAG = ForecastFragment.class.getSimpleName();
    ArrayAdapter<String> mForecastAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mForecastAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String forecast = mForecastAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void updateWeather() {
        SharedPreferences preferences = PreferenceManager .getDefaultSharedPreferences(getActivity());
        String location = preferences.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));

        final String units = "metric";
        int numDays = 7;

        final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
        final String QUERY_PARAM = "q";
        final String UNITS_PARAM = "units";
        final String DAYS_PARAM = "cnt";

        Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, location)
                .appendQueryParameter(UNITS_PARAM, units)
                .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                .build();
        String url = builtUri.toString();
        Log.d(TAG, "Built URI: " + url);

        GsonRequest<WeatherData> request = new GsonRequest<>(
                url,
                WeatherData.class,
                null,
                new Response.Listener<WeatherData>() {
                    @Override
                    public void onResponse(WeatherData response) {
                        mForecastAdapter.clear();
                        SharedPreferences sharedPrefs =
                                PreferenceManager.getDefaultSharedPreferences(getActivity());
                        String unitsType = sharedPrefs.getString(
                                getString(R.string.pref_units_key),
                                getString(R.string.pref_units_metric));
                        if(unitsType.equals(getString(R.string.pref_units_imperial))){
                            response.setUnitsType(WeatherData.TYPE_IMPERIAL);
                        }
                        String[] forecastStr = response.getForecastString();
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
                            mForecastAdapter.addAll(Arrays.asList(forecastStr));
                        }else{
                            for (String dayForecastStr : forecastStr) {
                                mForecastAdapter.add(dayForecastStr);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        ApplicationController.getInstance().getRequestQueue().add(request);
    }


}