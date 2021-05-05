package com.bletpms.app.ui.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SeekBarPreference;

import com.bletpms.app.R;

import java.util.Locale;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.SummaryProvider<androidx.preference.ListPreference> {

    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        Toolbar toolbar = findViewById(R.id.settingsToolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public CharSequence provideSummary(ListPreference preference) {
        String key = preference.getKey();
        if (key != null)
            if (key.matches("theme|temperature_unit|pressure_unit"))
                return preference.getEntry();
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private static final int minC = 60;

        private static final int minF = 140;

        private static final int maxC = 80;

        private static final int maxF = 176;

        private String temperatureLastUnit = "";
        private String pressureLastUnit = "";

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            ListPreference temperaturePreference = findPreference("temperature_unit");
            temperaturePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    temperatureLastUnit = ((ListPreference)preference).getValue();
                    return true;
                }
            });
            temperaturePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String newTemperatureUnitValue = String.valueOf(newValue);
                    if (!temperatureLastUnit.matches(newTemperatureUnitValue))
                        updateTemperatureSeekBarUnits(newTemperatureUnitValue, false);
                    return true;
                }
            });

            setTemperatureSeekBarBounds();

           ListPreference pressurePreference = findPreference("pressure_unit");
           pressurePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    pressureLastUnit = ((ListPreference)preference).getValue();
                    return true;
                }
            });
            pressurePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (!pressureLastUnit.matches(String.valueOf(newValue))){
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(requireContext());
                        final String[] pressureInitialValues = getResources().getStringArray(R.array.initial_pressure_values_bar);
                        float currentLowerValue = pref.getFloat("pressure_lower_value", Float.parseFloat(pressureInitialValues[0]));
                        float currentUpperValue = pref.getFloat("pressure_upper_value", Float.parseFloat(pressureInitialValues[1]));
                        float newLowerPressureValue = doPressureConversion(pressureLastUnit, String.valueOf(newValue), currentLowerValue);
                        float newUpperPressureValue = doPressureConversion(pressureLastUnit, String.valueOf(newValue), currentUpperValue);
                        Log.i(TAG, "Conversion: " + pressureLastUnit + " ---> " + newValue + "____ [" + currentLowerValue + ", "+currentUpperValue+"] ---> ["+ newLowerPressureValue + ", " + newUpperPressureValue + "]");
                        pref.edit().putFloat("pressure_lower_value", newLowerPressureValue).apply();
                        pref.edit().putFloat("pressure_upper_value", newUpperPressureValue).apply();
                    }

                    return true;
                }
            });

            /*Preference pressureLimitsPreference = findPreference("pressure_limits");
            pressureLimitsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent();
                    intent.setClassName(getContext(), "com.bletpms.app.ui.settings.PressureLimitsActivity");
                    startActivity(intent);
                    return false;
                }
            });*/

            Preference resetPreference = findPreference("reset_settings");
            resetPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(R.string.reset_settings_dialog)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(requireContext());
                                    pref.edit().clear().apply();
                                    getPreferenceScreen().removeAll();
                                    onCreatePreferences(savedInstanceState, rootKey);
                                    final String[] pressureInitialValues = getResources().getStringArray(R.array.initial_pressure_values_bar);
                                    pref.edit().putFloat("pressure_lower_value", pref.getFloat("pressure_lower_value", Float.parseFloat(pressureInitialValues[0]))).apply();
                                    pref.edit().putFloat("pressure_upper_value", pref.getFloat("pressure_upper_value", Float.parseFloat(pressureInitialValues[1]))).apply();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).show();
                    return false;
                }
            });

            Preference aboutPreference = findPreference("about");
            aboutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    DialogFragment dialog = new AboutDialog();
                    dialog.show(getParentFragmentManager(),"About dialog");
                    return false;
                }
            });
        }

        private void setTemperatureSeekBarBounds() {
            String tempUnits = PreferenceManager.getDefaultSharedPreferences(getContext())
                    .getString("temperature_unit", getString(R.string.temperature_unit_def_value));
            updateTemperatureSeekBarUnits(tempUnits, true);
        }

        private void updateTemperatureSeekBarUnits(String newTemperatureUnitValue, boolean onCreatePreference){

            SeekBarPreference temperatureSeekBarPreference = findPreference("temperature_upper_limit");
            int min, max, value;
            switch (newTemperatureUnitValue){
                case "celsius":
                    min = minC;
                    max = maxC;
                    value = fahrenheitToCelsius(temperatureSeekBarPreference.getValue());
                    break;
                case "fahrenheit":
                    min = minF;
                    max = maxF;
                    value = celsiusToFahrenheit(temperatureSeekBarPreference.getValue());
                    break;
                default:
                    min = minC;
                    max = maxC;
                    value = 65;
            }

            temperatureSeekBarPreference.setMax(200);
            temperatureSeekBarPreference.setMin(0);

            temperatureSeekBarPreference.setMax(max);
            temperatureSeekBarPreference.setMin(min);

            if (!onCreatePreference)
                temperatureSeekBarPreference.setValue(value);
        }

        private int fahrenheitToCelsius(int value){
            return (int)(( 5 *(value - 32)) / 9);
        }

        private int celsiusToFahrenheit(int value){
            return (int)(( 9 * value + (32 * 5)) / 5);
        }

        private float doPressureConversion(String oldUnit, String newUnit, float value) {
            float newValue = 0;
            if (oldUnit.matches("BAR")){
                if (newUnit.matches("KPA")){
                    newValue = value * 100;
                } else {
                    newValue = value * 14.504F;
                }
            }
            if (oldUnit.matches("KPA")){
                if (newUnit.matches("BAR")){
                    newValue = value / 100;
                } else {
                    newValue = value / 6.895F;
                }
            }
            if (oldUnit.matches("PSI")){
                if (newUnit.matches("BAR")){
                    newValue = value / 14.504F;
                } else {
                    newValue = value * 6.895F;
                }
            }
            return Float.parseFloat(String.format(Locale.getDefault(), "%.1f", newValue).replace(",", "."));
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key != null && sharedPreferences != null) {
            if (key.equals("theme")) {
                final String[] darkModeValues = getResources().getStringArray(R.array.theme_values);
                // The apps theme is decided depending upon the saved preferences on app startup
                String pref = PreferenceManager.getDefaultSharedPreferences(this)
                        .getString("theme", getString(R.string.theme_def_value));
                // Comparing to see which preference is selected and applying those theme settings
                if (pref.equals(darkModeValues[0]))
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                if (pref.equals(darkModeValues[1]))
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                if (pref.equals(darkModeValues[2]))
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }
    }
}