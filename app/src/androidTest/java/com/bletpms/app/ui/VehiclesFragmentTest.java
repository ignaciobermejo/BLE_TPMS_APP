package com.bletpms.app.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
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

import java.util.Objects;

import static com.schibsted.spain.barista.assertion.BaristaBackgroundAssertions.assertHasBackground;
import static com.schibsted.spain.barista.assertion.BaristaClickableAssertions.assertClickable;
import static com.schibsted.spain.barista.assertion.BaristaHintAssertions.assertHint;
import static com.schibsted.spain.barista.assertion.BaristaImageViewAssertions.assertHasAnyDrawable;
import static com.schibsted.spain.barista.assertion.BaristaImageViewAssertions.assertHasDrawable;
import static com.schibsted.spain.barista.assertion.BaristaListAssertions.assertCustomAssertionAtPosition;
import static com.schibsted.spain.barista.assertion.BaristaRecyclerViewAssertions.assertRecyclerViewItemCount;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickBack;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaDialogInteractions.clickDialogPositiveButton;
import static com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.clearText;
import static com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo;
import static com.schibsted.spain.barista.interaction.BaristaListInteractions.clickListItem;
import static com.schibsted.spain.barista.interaction.BaristaMenuClickInteractions.clickMenu;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.*;
import static com.bletpms.app.testCommon.TestUtil.*;

@RunWith(AndroidJUnit4.class)
public class VehiclesFragmentTest extends BaseUITest {

    @Before
    public void setUp(){
        super.setUp(false);
        addVehicleAndDeleteOthers("test vehicle", activityRule.getActivity());
    }

    @Test
    public void checkBaseElements() {
        clickOn(R.id.navigation_vehicles);

        assertRecyclerViewItemCount(R.id.recyclerView, 1);
        assertDisplayed("test vehicle");

        longClickOnRecyclerViewItem(0);

        assertDisplayed("1 selected");

        clickBack();
        clickOn(R.id.fab);

        assertHint(R.id.deviceIdEditText, R.string.vehicle_name);
        assertRecyclerViewItemCount(R.id.recyclerViewNewVehicle, 11);
    }

    @Test
    public void addVehicles() {
        clickOn(R.id.navigation_vehicles);
        clickOn(R.id.fab);
        writeTo(R.id.deviceIdEditText, "Four Wheels");
        clickListItem(R.id.recyclerViewNewVehicle, 0);
        clickOn(R.id.pairDeviceSaveButton);

        assertRecyclerViewItemCount(R.id.recyclerView, 2);
        assertDisplayed("Four Wheels");

        clickOn(R.id.fab);
        writeTo(R.id.deviceIdEditText, "Motorbike");
        clickListItem(R.id.recyclerViewNewVehicle, 1);
        clickOn(R.id.pairDeviceSaveButton);

        assertRecyclerViewItemCount(R.id.recyclerView, 3);
        assertDisplayed("Motorbike");
    }

    @Test
    public void addVehicle_wrongInput_showToast() {
        clickOn(R.id.navigation_vehicles);
        clickOn(R.id.fab);
        clickOn(R.id.pairDeviceSaveButton);

        assertToast(activityRule.getActivity(), "Please, enter a vehicle name");

        sleep(2, SECONDS);
        writeTo(R.id.deviceIdEditText, "test vehicle");
        clickOn(R.id.pairDeviceSaveButton);

        assertToast(activityRule.getActivity(), "Please, select vehicle type");
    }

    @Test
    public void deleteOneVehicleAndMultipleVehicles() {
        clickOn(R.id.navigation_vehicles);

        assertRecyclerViewItemCount(R.id.recyclerView, 1);

        addVehicle("First delete");

        assertRecyclerViewItemCount(R.id.recyclerView, 2);

        changeMainVehicle(0);
        longClickOnRecyclerViewItem(1);
        clickOn(R.id.delete_item_contextual_menu);
        clickDialogPositiveButton();

        assertRecyclerViewItemCount(R.id.recyclerView, 1);

        addVehicle("Second delete 1");
        addVehicle("Second delete 2");

        assertRecyclerViewItemCount(R.id.recyclerView, 3);

        changeMainVehicle(0);
        longClickOnRecyclerViewItem(1);
        clickListItem(2);
        clickOn(R.id.delete_item_contextual_menu);
        clickDialogPositiveButton();

        assertRecyclerViewItemCount(R.id.recyclerView, 1);
    }

    @Test
    public void deleteOneVehicleAndMultipleVehicles_mainSelected_showErrorMessage() {
        clickOn(R.id.navigation_vehicles);

        assertRecyclerViewItemCount(R.id.recyclerView, 1);

        addVehicle("test vehicle 2");

        assertRecyclerViewItemCount(R.id.recyclerView, 2);

        longClickOnRecyclerViewItem(1);
        clickMenu(R.id.delete_item_contextual_menu);

        assertDisplayed("Main vehicle cannot be deleted");

        clickDialogPositiveButton();

        longClickOnRecyclerViewItem(1);
        clickListItem(0);
        clickMenu(R.id.delete_item_contextual_menu);

        assertDisplayed("Main vehicle cannot be deleted");
    }

    @Test
    public void editVehicleName() {
        clickOn(R.id.navigation_vehicles);

        assertDisplayed("test vehicle");

        longClickOnRecyclerViewItem(0);
        clickMenu(R.id.edit_item_contextual_menu);
        writeTo(R.id.deviceIdEditText, "NEW test vehicle");
        clickOn(R.id.pairDeviceSaveButton);

        assertDisplayed("NEW test vehicle");
    }

    @Test
    public void editVehicleName_wrongInput_showToast() {
        clickOn(R.id.navigation_vehicles);

        assertDisplayed("test vehicle");

        longClickOnRecyclerViewItem(0);
        clickMenu(R.id.edit_item_contextual_menu);
        clearText(R.id.deviceIdEditText);
        clickOn(R.id.pairDeviceSaveButton);

        assertToast(activityRule.getActivity(), "Please, enter a vehicle name");
    }

    @Test
    public void changeMainVehicle_itemOptionAndMenuOption() {
        clickOn(R.id.navigation_vehicles);

        assertMainVehicle(0);

        addVehicle("New main");

        assertMainVehicle(1);
        assertNotMainVehicle(0);

        changeMainVehicle(0);

        assertMainVehicle(0);
        assertNotMainVehicle(1);

        longClickOnRecyclerViewItem(1);
        clickOn(R.id.main_item_contextual_menu);

        assertMainVehicle(1);
        assertNotMainVehicle(0);
    }
}