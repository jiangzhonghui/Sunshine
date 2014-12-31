package com.ivkil.sunshine;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ivkil.sunshine.application.ApplicationController;
import com.ivkil.sunshine.data.WeatherContract;
import com.ivkil.sunshine.models.City;
import com.ivkil.sunshine.models.WeatherData;
import com.ivkil.sunshine.models.WeatherListItem;
import com.ivkil.sunshine.networking.GsonRequest;

import java.util.Calendar;
import java.util.Vector;


public class TemporaryUpdate {

    private Context mContext;

    public TemporaryUpdate(Context context){
        mContext = context;
    }
    public void updateWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String location = preferences.getString(mContext.getString(R.string.pref_location_key),
                mContext.getString(R.string.pref_location_default));

        final String units = "metric";
        int numDays = 14;

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

        GsonRequest<WeatherData> request = new GsonRequest<>(
                url,
                WeatherData.class,
                null,
                new Response.Listener<WeatherData>() {
                    @Override
                    public void onResponse(WeatherData response) {
                        updateFinished(response);
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

    private long addLocation(String locationSetting, String cityName, double lat, double lon) {
        long locationId;

        Cursor locationCursor = mContext.getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                new String[]{WeatherContract.LocationEntry._ID},
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
                new String[]{locationSetting},
                null);

        if (locationCursor.moveToFirst()) {
            int locationIdIndex = locationCursor.getColumnIndex(WeatherContract.LocationEntry._ID);
            locationId = locationCursor.getLong(locationIdIndex);
        } else {
            ContentValues locationValues = new ContentValues();

            locationValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, cityName);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, locationSetting);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, lat);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, lon);

            Uri insertedUri = mContext.getContentResolver().insert(
                    WeatherContract.LocationEntry.CONTENT_URI,
                    locationValues
            );

            locationId = ContentUris.parseId(insertedUri);
        }

        return locationId;
    }

    private void updateFinished(final WeatherData response) {
        new Runnable() {

            @Override
            public void run() {
                //units
                if (!Utility.isMetric(mContext)) {
                    response.setUnitsType(WeatherData.TYPE_IMPERIAL);
                }
                String locationQuery = Utility.getPreferredLocation(mContext);
                //city
                City city = response.getCity();
                long locationId = addLocation(
                        locationQuery,
                        city.getName(),
                        city.getLocation().getLatitude(),
                        city.getLocation().getLongitude()
                );
                WeatherListItem[] weatherArray = response.getList();
                Vector<ContentValues> cVVector = new Vector<>(weatherArray.length);
                for (WeatherListItem weather : weatherArray) {
                    ContentValues weatherValues = new ContentValues();

                    weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationId);
                    weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATETEXT, WeatherContract.getDbDateString(weather.getDateTime()));
                    weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, weather.getHumidity());
                    weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, weather.getPressure());
                    weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, weather.getSpeed());
                    weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, weather.getDirection());
                    weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, weather.getTemperature().getHigh());
                    weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, weather.getTemperature().getLow());
                    weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, weather.getWeather()[0].getDescribtion());
                    weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weather.getWeather()[0].getId());

                    cVVector.add(weatherValues);
                }

                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    mContext.getContentResolver().bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI, cvArray);
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -1);
                    String yesterdayDate = WeatherContract.getDbDateString(cal.getTime());
                    mContext.getContentResolver().delete(WeatherContract.WeatherEntry.CONTENT_URI,
                            WeatherContract.WeatherEntry.COLUMN_DATETEXT + " <= ?",
                            new String[]{yesterdayDate});
                }

            }
        }.run();
    }
}
