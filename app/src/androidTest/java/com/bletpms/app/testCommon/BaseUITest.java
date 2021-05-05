package com.bletpms.app.testCommon;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.bletpms.app.MainActivity;
import com.schibsted.spain.barista.interaction.PermissionGranter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import static com.bletpms.app.testCommon.TestUtil.allowLocationPermissionsIfNeeded;
import static com.bletpms.app.testCommon.TestUtil.enableBluetoothIfNeeded;
import static com.schibsted.spain.barista.interaction.BaristaDialogInteractions.clickDialogPositiveButton;

public class BaseUITest {

    private static boolean setUpIsDone = false;

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    public void setUp(boolean executeOnce){
        if (setUpIsDone) {
            return;
        }
        enableBluetoothIfNeeded();
        allowLocationPermissionsIfNeeded();

        setUpIsDone = executeOnce;
    }
}
