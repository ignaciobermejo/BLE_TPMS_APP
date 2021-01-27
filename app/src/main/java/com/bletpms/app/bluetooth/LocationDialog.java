package com.bletpms.app.bluetooth;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.bletpms.app.MainActivity;
import com.bletpms.app.R;

public class LocationDialog extends DialogFragment {

    private int LOCATION_PERMISSION_REQUEST_CODE = 0;

    public LocationDialog(int LOCATION_PERMISSION_REQUEST_CODE) {
        this.LOCATION_PERMISSION_REQUEST_CODE=LOCATION_PERMISSION_REQUEST_CODE;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.location_message)
                .setTitle(R.string.location_title)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((MainActivity)getActivity()).requestPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                        LOCATION_PERMISSION_REQUEST_CODE);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}

