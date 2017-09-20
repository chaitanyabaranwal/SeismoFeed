package com.example.android.seismofeed;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by chaitanya on 28/03/17.
 */

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

    }

    public static class EarthquakePreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference minMagnitude = findPreference(getString(R.string.min_mag_key));
            bindPreferenceSummaryToValue(minMagnitude);

            Preference orderBy = findPreference(getString(R.string.order_by_key));
            bindPreferenceSummaryToValue(orderBy);

            Preference maxLimit = findPreference(getString(R.string.maxLimitKey));
            bindPreferenceSummaryToValue(maxLimit);

            Preference startDate = findPreference(getString(R.string.startDateKey));
            bindPreferenceSummaryToValue(startDate);

            Preference endDate = findPreference(getString(R.string.endDateKey));
            bindPreferenceSummaryToValue(endDate);

        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int indexOf = listPreference.findIndexOfValue(stringValue);
                if (indexOf >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[indexOf]);
                }
            }
            else
                preference.setSummary(stringValue);
            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferencesString = sharedPreferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferencesString);
        }

    }
}
