package com.bletpms.app.ui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.bletpms.app.MainActivity;
import com.bletpms.app.R;
import com.bletpms.app.testCommon.BaseUITest;
import com.bletpms.app.testCommon.TestUtil;
import com.schibsted.spain.barista.interaction.PermissionGranter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.bletpms.app.testCommon.TestUtil.addVehicle;
import static com.schibsted.spain.barista.assertion.BaristaBackgroundAssertions.assertHasBackground;
import static com.schibsted.spain.barista.assertion.BaristaClickableAssertions.assertClickable;
import static com.schibsted.spain.barista.assertion.BaristaImageViewAssertions.assertHasAnyDrawable;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotContains;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickBack;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaDialogInteractions.clickDialogPositiveButton;
import static com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.clearText;
import static com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo;
import static com.schibsted.spain.barista.interaction.BaristaListInteractions.clickListItem;

@RunWith(AndroidJUnit4.class)
public class PairFragmentTest extends BaseUITest {

    @Before
    public void setUp(){
        super.setUp(true);
        addVehicle("test vehicle");
    }

    @Test
    public void checkBaseElements() {
        clickOn(R.id.navigation_pair);
        assertHasBackground(R.id.pairView, R.drawable.backgroung_1);
        assertHasAnyDrawable(R.id.vehicleImageView);
        assertDisplayed(R.id.card1);
        assertClickable(R.id.card1);
        assertDisplayed(R.id.card2);
        assertClickable(R.id.card2);
        assertDisplayed(R.id.card3);
        assertClickable(R.id.card3);
        assertDisplayed(R.id.card4);
        assertClickable(R.id.card4);
        assertDisplayed(R.string.pair_bind);
    }

    @Test
    public void checkManualBind() {
        clickOn(R.id.navigation_pair);
        clickOn(R.id.card1);
        clickOn(R.id.pairDeviceManualButton);
        writeTo(R.id.deviceIdEditText, "000000");
        clickOn(R.id.pairDeviceSaveButton);

        assertDisplayed("000000");
    }

    @Test
    public void checkAutoBind() {
        clickOn(R.id.navigation_pair);
        clickOn(R.id.card1);
        clickOn(R.id.pairDeviceAutoButton);
        clickOn(R.id.pairDeviceSearchButton);

        assertDisplayed(R.id.autoSearchDialog);

        clickBack();
    }

    @Test
    public void checkEditAndUnbind() {
        clickOn(R.id.navigation_pair);
        clickOn(R.id.card1);
        clickOn(R.id.pairDeviceManualButton);
        writeTo(R.id.deviceIdEditText, "000000");
        clickOn(R.id.pairDeviceSaveButton);

        assertDisplayed("000000");

        clickOn(R.id.card1);
        clickOn(R.id.pairDeviceManualButton);
        clearText(R.id.deviceIdEditText);
        writeTo(R.id.deviceIdEditText, "000001");
        clickOn(R.id.pairDeviceSaveButton);

        assertDisplayed("000001");

        clickOn(R.id.card1);
        clickOn(R.id.pairDeviceUnbindButton);
        clickDialogPositiveButton();

        assertNotContains(R.id.card1,"000001");
    }
}