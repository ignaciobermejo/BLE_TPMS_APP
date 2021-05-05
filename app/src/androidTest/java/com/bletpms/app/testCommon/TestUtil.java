package com.bletpms.app.testCommon;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Root;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.bletpms.app.R;
import com.bletpms.app.database.Vehicle;
import com.schibsted.spain.barista.interaction.PermissionGranter;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Objects;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.schibsted.spain.barista.assertion.BaristaListAssertions.assertCustomAssertionAtPosition;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.longClickOn;
import static com.schibsted.spain.barista.interaction.BaristaDialogInteractions.clickDialogPositiveButton;
import static com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.clearText;
import static com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo;
import static com.schibsted.spain.barista.interaction.BaristaListInteractions.clickListItem;
import static com.schibsted.spain.barista.interaction.BaristaListInteractions.clickListItemChild;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleepThread;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class TestUtil {

    public static Vehicle createVehicle (String name){
        return new Vehicle(name, "4",4);
    }

    public static void enableBluetoothIfNeeded(){
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()){
            UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            UiObject okButton = device.findObject(new UiSelector()
                    .textMatches("PERMITIR|ALLOW")
                    .className("android.widget.Button"));
            try {
                if(okButton.exists() && okButton.isEnabled()) {
                    okButton.click();
                    //sleep(5000);
                }
            } catch (UiObjectNotFoundException e) {
                Log.e("UIAutomator", "There is no bluetooth dialog to interact with", e);
            }
        }
    }

    public static void allowLocationPermissionsIfNeeded(){
        if (!(ContextCompat.checkSelfPermission(ApplicationProvider.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)){
            sleepThread(3000);
            //sleep(5000);
            clickDialogPositiveButton();
            PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    public static void addVehicle(String name){
        clickOn(R.string.title_vehicles);
        clickOn(R.id.fab);
        writeTo(R.id.deviceIdEditText, name);
        clickListItem(R.id.recyclerViewNewVehicle, 0);
        clickOn(R.id.pairDeviceSaveButton);
    }

    public static void addVehicleAndDeleteOthers(String name, Activity activity){
        clickOn(R.id.navigation_vehicles);
        RecyclerView recyclerView = activity.findViewById(R.id.recyclerView);
        int itemsCount = Objects.requireNonNull(recyclerView.getAdapter()).getItemCount();
        addVehicle(name);
        longClickOnRecyclerViewItem(itemsCount);
        for (int i = 0; i < itemsCount+1; i++) {
            clickListItem(R.id.recyclerView, i);
        }
        clickOn(R.id.delete_item_contextual_menu);
        clickDialogPositiveButton();
    }

    public static void longClickOnRecyclerViewItem(int itemPosition){
        sleep(500);
        onView(ViewMatchers.withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(itemPosition, longClick()));
    }

    public static void changeMainVehicle(int newMainVehiclePosition){
        clickListItemChild(R.id.recyclerView, newMainVehiclePosition, R.id.mainImageView);
    }

    public static void assertMainVehicle(int itemPosition){
        assertCustomAssertionAtPosition(R.id.recyclerView, itemPosition, R.id.mainImageView, ViewAssertions.matches(withDrawable(R.drawable.ic_baseline_star_24)));
    }

    public static void assertNotMainVehicle(int itemPosition){
        assertCustomAssertionAtPosition(R.id.recyclerView, itemPosition, R.id.mainImageView, ViewAssertions.matches(withDrawable(R.drawable.ic_baseline_star_outline_24)));
    }

    public static Matcher<View> withDrawable(final int resourceId) {
        return new DrawableMatcher(resourceId);
    }

    public static void assertToast(Activity activity, String message){
        onView(withText(message)).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    public static class ToastMatcher extends TypeSafeMatcher<Root> {

        @Override
        public void describeTo(Description description) {
            description.appendText("is toast");
        }

        @Override
        public boolean matchesSafely(Root root) {
            int type = root.getWindowLayoutParams().get().type;
            if ((type == WindowManager.LayoutParams.TYPE_TOAST)) {
                IBinder windowToken = root.getDecorView().getWindowToken();
                IBinder appToken = root.getDecorView().getApplicationWindowToken();
                return windowToken == appToken;
            }
            return false;
        }
    }
}
