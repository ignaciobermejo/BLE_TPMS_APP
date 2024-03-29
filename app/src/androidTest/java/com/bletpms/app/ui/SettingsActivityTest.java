package com.bletpms.app.ui;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.test.espresso.contrib.RecyclerViewActions;

import com.bletpms.app.R;
import com.bletpms.app.testCommon.BaseUITest;
import com.schibsted.spain.barista.rule.cleardata.ClearPreferencesRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Locale;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.bletpms.app.testCommon.TestUtil.addVehicleAndDeleteOthers;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickBack;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaDialogInteractions.clickDialogPositiveButton;
import static com.google.common.truth.Truth.assertThat;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;

public class SettingsActivityTest extends BaseUITest {

    // Clear all app's SharedPreferences
    @Rule public ClearPreferencesRule clearPreferencesRule = new ClearPreferencesRule();

    @Before
    public void setUp(){
        super.setUp(true);
        addVehicleAndDeleteOthers("test vehicle", activityRule.getActivity());
    }

    @Test
    public void checkThemePreference() {
        clickOn(R.id.navigation_home);
        clickOn(R.id.navigation_settings);

        int currentAppTheme = AppCompatDelegate.getDefaultNightMode();
        assertThat(currentAppTheme).isEqualTo(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        clickOn(R.string.theme_title);
        clickOn(R.string.theme_entry_light);
        sleep(1000);

        currentAppTheme = AppCompatDelegate.getDefaultNightMode();
        assertThat(currentAppTheme).isEqualTo(AppCompatDelegate.MODE_NIGHT_NO);

        clickOn(R.string.theme_title);
        clickOn(R.string.theme_entry_dark);
        sleep(1000);

        currentAppTheme = AppCompatDelegate.getDefaultNightMode();
        assertThat(currentAppTheme).isEqualTo(AppCompatDelegate.MODE_NIGHT_YES);
    }

    @Test
    public void checkTemperaturePreferences() {
        clickOn(R.id.navigation_home);
        clickOn(R.id.navigation_settings);

        assertDisplayed("ºC");
        assertDisplayed("65");

        clickOn("ºC");
        clickOn("ºF");

        assertDisplayed("ºF");
        assertDisplayed("149");

        clickBack();
        assertDisplayed("ºF");
    }

    @Test
    public void checkPressurePreferences() {
        clickOn(R.id.navigation_home);
        clickOn(R.id.navigation_settings);

        assertDisplayed("BAR");

        clickOn(R.string.pressure_limits);

        assertDisplayed(String.format(Locale.getDefault(), "%.1f", 2F));
        assertDisplayed(String.format(Locale.getDefault(), "%.1f", 3F));

        clickBack();
        clickOn("BAR");
        clickOn("KPA");
        clickOn(R.string.pressure_limits);

        assertDisplayed(String.format(Locale.getDefault(), "%.1f", 200F));
        assertDisplayed(String.format(Locale.getDefault(), "%.1f", 300F));

        clickBack();
        clickOn("KPA");
        clickOn("PSI");
        clickOn(R.string.pressure_limits);

        assertDisplayed(String.format(Locale.getDefault(), "%.1f", 29F));
        assertDisplayed(String.format(Locale.getDefault(), "%.1f", 43.5F));

        clickBack();
        clickBack();

        assertDisplayed("PSI");
    }

    @Test
    public void checkAboutPreference() {
        clickOn(R.id.navigation_home);
        clickOn(R.id.navigation_settings);
        onView(withId(androidx.preference.R.id.recycler_view))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(R.string.about_title)),
                        click()));

        assertDisplayed(R.string.about_message);
        assertDisplayed(R.string.about_link);
    }

    @Test
    public void checkResetSettingsPreference() {
        clickOn(R.id.navigation_home);
        clickOn(R.id.navigation_settings);

        clickOn("ºC");
        clickOn("ºF");

        assertDisplayed("ºF");
        assertDisplayed("149");

        clickOn("BAR");
        clickOn("KPA");

        assertDisplayed("KPA");

        clickOn(R.string.pressure_limits);

        assertDisplayed(String.format(Locale.getDefault(), "%.1f", 200F));
        assertDisplayed(String.format(Locale.getDefault(), "%.1f", 300F));

        clickBack();

        clickOn(R.string.reset_settings);
        clickDialogPositiveButton();

        assertDisplayed("ºC");
        assertDisplayed("65");
        assertDisplayed("BAR");

        clickOn(R.string.pressure_limits);

        assertDisplayed(String.format(Locale.getDefault(), "%.1f", 2F));
        assertDisplayed(String.format(Locale.getDefault(), "%.1f", 3F));


    }
}