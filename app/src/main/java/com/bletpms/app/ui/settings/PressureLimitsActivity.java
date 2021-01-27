package com.bletpms.app.ui.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.bletpms.app.R;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PressureLimitsActivity extends AppCompatActivity {

    private static final double maxBarLower = 2.4D;

    private static final double maxBarUpper = 3.6D;

    private static final double minBarLower = 0.1D;

    private static final double minBarUpper = 2.8D;

    private static final double defaultLowerBar = 2.0D;

    private static final double defaultUpperBar = 3.0D;

    private static final int maxKpaLower = 240;

    private static final int maxKpaUpper = 360;

    private static final int minKpaLower = 10;

    private static final int minKpaUpper = 280;

    private static final double defaultLowerKpa = 204;

    private static final double defaultUpperKpa = 306;

    private static final double minPsiLower = 14.5D;

    private static final double minPsiUpper = 40.6D;

    private static final double maxPsiLower = 21.75D;

    private static final double maxPsiUpper = 52.2D;

    private static final double defaultLowerPsi = 29.0D;

    private static final double defaultUpperPsi = 43.5D;

    SharedPreferences preferences;

    RangeSlider slider;

    TextView lowerTextView;
    TextView upperTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pressure_limits);

        Toolbar toolbar = findViewById(R.id.pressureLimitsToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String currentPressureUnit = preferences.getString("pressure_unit", getString(R.string.pressure_unit_def_value));

        TextView unitsTextView = findViewById(R.id.pressureLimitsUnit);
        unitsTextView.setText(currentPressureUnit);

        lowerTextView = findViewById(R.id.pressureLowerLimit);
        upperTextView = findViewById(R.id.pressureUpperLimit);

        slider = findViewById(R.id.pressureSlider);
        /*slider.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(),"%.1f",value);
            }
        });*/
        //slider.setMinSeparation(20);
        setPressureValues(currentPressureUnit);
        setTextViews();

        slider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                setTextViews();
            }
        });
    }

    private void setTextViews() {
        lowerTextView.setText(String.valueOf(slider.getValues().get(1)));
        upperTextView.setText(String.valueOf(slider.getValues().get(0)));
    }

    private void setPressureValues(String currentPressureUnit) {
        final String[] pressureUnitValues = getResources().getStringArray(R.array.pressure_values);

        slider.setValueFrom(0);
        slider.setValueTo(400);

        if (currentPressureUnit.equals(pressureUnitValues[0])){         //BAR
            //slider.setMinSeparationValue(0.5F);
            slider.setStepSize(0.1F);
            slider.setValueFrom((float) minBarLower);
            slider.setValueTo((float) maxBarUpper);
            slider.setValues(preferences.getFloat("pressure_upper_value", (float) defaultUpperBar), preferences.getFloat("pressure_lower_value", (float) defaultLowerBar));
        }
        if (currentPressureUnit.equals(pressureUnitValues[1])){         //KPA
            //slider.setMinSeparationValue(50F);
            slider.setStepSize(1F);
            slider.setValueFrom((float) minKpaLower);
            slider.setValueTo((float) maxKpaUpper);
            slider.setValues(preferences.getFloat("pressure_upper_value", (float) defaultUpperKpa), preferences.getFloat("pressure_lower_value", (float) defaultLowerKpa));
        }
        if (currentPressureUnit.equals(pressureUnitValues[2])){         //PSI
            //slider.setMinSeparationValue(7F);
            slider.setStepSize(0.1F);
            slider.setValueFrom((float) minPsiLower);
            slider.setValueTo((float) maxPsiUpper);
            slider.setValues(preferences.getFloat("pressure_upper_value", (float) defaultUpperPsi), preferences.getFloat("pressure_lower_value", (float) defaultLowerPsi));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            preferences.edit().putFloat("pressure_lower_value", slider.getValues().get(1)).apply();
            preferences.edit().putFloat("pressure_upper_value", slider.getValues().get(0)).apply();
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        preferences.edit().putFloat("pressure_lower_value", slider.getValues().get(1)).apply();
        preferences.edit().putFloat("pressure_upper_value", slider.getValues().get(0)).apply();
        super.onBackPressed();
    }
}