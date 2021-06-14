package com.bletpms.app;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(
        {BluetoothServiceTest.class,
                DataParserTest.class,
                UnitConverterTest.class,
                VehicleTypesTest.class}
)
public class aUnitTests_runAll {
}
