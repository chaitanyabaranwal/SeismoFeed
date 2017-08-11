package com.example.android.seismofeed;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.content.Loader;
import android.os.Bundle;
import android.app.LoaderManager.LoaderCallbacks;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.seismofeed.AboutActivity;

import java.util.ArrayList;

import static android.os.Build.VERSION_CODES.M;
import static android.webkit.ConsoleMessage.MessageLevel.LOG;
import static com.example.android.seismofeed.QueryUtils.LOG_TAG;

public class EarthquakeActivity extends AppCompatActivity implements LoaderCallbacks<ArrayList<Earthquake>> {

    private static final String JSON_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query";

    private EarthquakeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        getLoaderManager().initLoader(1,null,this);

        final ListView earthquakeListView = (ListView) findViewById(R.id.list);

        adapter = new EarthquakeAdapter(getApplicationContext(), new ArrayList<Earthquake>());

        earthquakeListView.setEmptyView(findViewById(R.id.empty_view));
        earthquakeListView.setAdapter(adapter);


        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                Earthquake currentEarthquake = adapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
    }


    @Override
    public Loader<ArrayList<Earthquake>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String minMagnitude = sharedPreferences.getString(getString(R.string.min_mag_key), getString(R.string.min_mag_value));
        String orderBy = sharedPreferences.getString(getString(R.string.order_by_key), getString(R.string.order_by_default));
        String maxLimit = sharedPreferences.getString(getString(R.string.maxLimitKey), getString(R.string.maxLimitValue));
        String startDate = sharedPreferences.getString(getString(R.string.startDateKey),getString(R.string.startDateValue));
        String endDate = sharedPreferences.getString(getString(R.string.endDateKey),getString(R.string.endDateValue));


        Uri baseUri = Uri.parse(JSON_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");


        if (!startDate.equals(""))
            uriBuilder.appendQueryParameter("starttime", startDate);
        if (!endDate.equals(""))
        uriBuilder.appendQueryParameter("endtime", endDate);


        uriBuilder.appendQueryParameter("limit", maxLimit);
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Earthquake>> loader, final ArrayList<Earthquake> earthquakes) {
        adapter.clear();

        TextView emptyView = (TextView) findViewById(R.id.empty_view);
        emptyView.setText(R.string.no_earthquake);

        ConnectivityManager checkNet = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = checkNet.getActiveNetworkInfo();
        if (activeNetwork == null || !activeNetwork.isConnectedOrConnecting())
            emptyView.setText(R.string.no_internet);

        ProgressBar loadingIndicator = (ProgressBar) findViewById(R.id.loadingIndicator);
        loadingIndicator.setVisibility(View.GONE);
        if (earthquakes != null && !earthquakes.isEmpty()) {
            adapter.addAll(earthquakes);
        }

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Earthquake>> loader) {
        adapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.settings_action) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        else if (id == R.id.about_settings) {
            Intent gotoAboutApp = new Intent(this, AboutActivity.class);
            startActivity(gotoAboutApp);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
