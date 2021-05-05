package com.bletpms.app;

import com.bletpms.app.ui.aUITests_runAll;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(
        {aUITests_runAll.class,
            DatabaseTest.class}
)
public class aInstrumentedTests_runAll {
}