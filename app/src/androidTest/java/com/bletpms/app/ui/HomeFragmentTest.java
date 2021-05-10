package com.bletpms.app.ui;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.bletpms.app.R;
import com.bletpms.app.testCommon.BaseUITest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.bletpms.app.testCommon.TestUtil.addVehicle;
import static com.schibsted.spain.barista.assertion.BaristaBackgroundAssertions.assertHasBackground;
import static com.schibsted.spain.barista.assertion.BaristaClickableAssertions.assertClickable;
import static com.schibsted.spain.barista.assertion.BaristaImageViewAssertions.assertHasAnyDrawable;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo;

@RunWith(AndroidJUnit4.class)
public class HomeFragmentTest extends BaseUITest {

    @Before
    public void setUp() {
        super.setUp(false);
        addVehicle("test vehicle");
    }

    @Test
    public void checkBaseElements() {
        clickOn(R.id.navigation_home);
        assertHasBackground(R.id.homeView, R.drawable.backgroung_1);
        assertHasAnyDrawable(R.id.vehicleImageView);
        assertDisplayed(R.id.card1);
        assertClickable(R.id.card1);
        assertDisplayed(R.id.card2);
        assertClickable(R.id.card2);
        assertDisplayed(R.id.card3);
        assertClickable(R.id.card3);
        assertDisplayed(R.id.card4);
        assertClickable(R.id.card4);
        assertDisplayed("BAR");
        assertDisplayed("ºC");
    }

    @Test
    public void checkWaitingForDataState() {
        clickOn(R.id.navigation_pair);
        clickOn(R.id.card1);
        clickOn(R.id.pairDeviceManualButton);
        writeTo(R.id.deviceIdEditText, "000000");
        clickOn(R.id.pairDeviceSaveButton);
        clickOn(R.id.navigation_home);

        assertDisplayed(R.id.progressIndicator);
    }

}