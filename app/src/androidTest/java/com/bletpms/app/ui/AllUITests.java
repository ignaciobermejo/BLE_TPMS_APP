package com.bletpms.app.ui;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(
        {HomeFragmentTest.class,
        PairFragmentTest.class,
        SettingsActivityTest.class,
        VehiclesFragmentTest.class}
)
public class AllUITests {
}
