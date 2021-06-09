package com.bletpms.app.ui;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.bletpms.app.R;
import com.bletpms.app.testCommon.BaseUITest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.schibsted.spain.barista.assertion.BaristaHintAssertions.assertHint;
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

        Context context = ApplicationProvider.getApplicationContext();
        assertDisplayed(context.getResources().getQuantityString(R.plurals.vehiclesSelected, 1, 1));

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

        assertToast(R.string.toast_vehicle_name);

        sleep(2, SECONDS);
        writeTo(R.id.deviceIdEditText, "test vehicle");
        clickOn(R.id.pairDeviceSaveButton);

        assertToast(R.string.toast_vehicle_type);
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

        assertDisplayed(R.string.cannot_delete_main);

        clickDialogPositiveButton();

        longClickOnRecyclerViewItem(1);
        clickListItem(0);
        clickMenu(R.id.delete_item_contextual_menu);

        assertDisplayed(R.string.cannot_delete_main);
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

        assertToast(R.string.toast_vehicle_name);
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