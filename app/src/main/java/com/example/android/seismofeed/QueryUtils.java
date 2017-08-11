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
package com.example.android.seismofeed;

import android.text.TextUtils;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    /** Sample JSON response for a USGS query */

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    public static ArrayList<Earthquake> fetchEarthquakeData(String JSON_URL) {

        URL url = createUrl(JSON_URL);

        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG,"Error closing Input Stream (makeHTTPRequest IO Exception)", e);
        }

        ArrayList<Earthquake> earthquakes = extractEarthquakeList(jsonResponse);

        return earthquakes;
    }

    private static URL createUrl(String JSON_URL) {
        URL url = null;
        try {
            url = new URL(JSON_URL);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error making a URL!", e);
        }

        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = "";
        if (url == null)
            return null;

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /*milliseconds*/);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            else {
                Log.e(LOG_TAG,"Error Response Code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException error!", e);
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
            if (inputStream != null)
                inputStream.close();
        }

        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        try {
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException error!", e);
        }

        return output.toString();
    }

    private static ArrayList<Earthquake> extractEarthquakeList(String jsonResponse) {
        ArrayList<Earthquake> earthquakes = new ArrayList<>();
        if (TextUtils.isEmpty(jsonResponse))
            return null;

        try {
            JSONObject baseJSONResponse = new JSONObject(jsonResponse);
            JSONArray features = baseJSONResponse.getJSONArray("features");

            if (features.length() > 0) {

                for (int i = 0; i < features.length(); i++) {
                    JSONObject currentFeatures = features.getJSONObject(i);
                    JSONObject properties = currentFeatures.getJSONObject("properties");

                    double magnitude = properties.getDouble("mag");
                    String location = properties.getString("place");
                    long time = properties.getLong("time");
                    String url = properties.getString("url");

                    earthquakes.add(new Earthquake(magnitude, location, time, url));
                }

             return earthquakes;
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing JSON!", e);
        }

        return null;
    }


}
