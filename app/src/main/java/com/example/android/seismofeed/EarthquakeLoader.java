package com.example.android.seismofeed;

import android.content.Context;
import android.content.AsyncTaskLoader;

import com.example.android.seismofeed.QueryUtils;

import java.util.ArrayList;

/**
 * Created by chaitanya on 25/03/17.
 */

public class EarthquakeLoader extends AsyncTaskLoader<ArrayList<Earthquake>> {

    private String mUrl;
    private final static String LOG_TAG = EarthquakeLoader.class.getName();

    public EarthquakeLoader(Context context,String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Earthquake> loadInBackground() {
        if (mUrl == null)
            return null;
        ArrayList<Earthquake> earthquakes = QueryUtils.fetchEarthquakeData(mUrl);
        return earthquakes;
    }


}
