package com.bletpms.app.testCommon;

import androidx.test.rule.ActivityTestRule;

import com.bletpms.app.MainActivity;

import org.junit.Rule;

import static com.bletpms.app.testCommon.TestUtil.allowLocationPermissionsIfNeeded;
import static com.bletpms.app.testCommon.TestUtil.enableBluetoothIfNeeded;

public class BaseUITest {

    private static boolean setUpIsDone = false;

    @Rule
    public final ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    public void setUp(boolean executeOnce){
        if (setUpIsDone) {
            return;
        }
        enableBluetoothIfNeeded();
        allowLocationPermissionsIfNeeded();

        setUpIsDone = executeOnce;
    }
}
