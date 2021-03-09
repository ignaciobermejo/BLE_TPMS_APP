package com.bletpms.app.ui.pair;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bletpms.app.R;
import com.bletpms.app.database.Vehicle;

public class PairDeviceManualDialog extends DialogFragment {

    private PairViewModel mPairViewModel;
    private Vehicle mainVehicle;
    private int selectedWheel;
    private PairDeviceDialog pairDeviceDialog;

    public PairDeviceManualDialog(Vehicle vehicle, PairViewModel model, int selectedWheel, PairDeviceDialog pairDeviceDialog) {
        this.mainVehicle = vehicle;
        this.mPairViewModel = model;
        this.selectedWheel = selectedWheel;
        this.pairDeviceDialog = pairDeviceDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root =inflater.inflate(R.layout.dialog_pair_device_manual, null);

        EditText deviceID = root.findViewById(R.id.deviceIdEditText);
        Button saveButton = root.findViewById(R.id.pairDeviceSaveButton);
        Button cancelButton = root.findViewById(R.id.pairDeviceCancelButton);

        if (mainVehicle.getDevice(selectedWheel) != null){
            deviceID.setText(mainVehicle.getDevice(selectedWheel));
        }
        deviceID.requestFocus();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deviceID.getText().toString().length() < 6){
                    Toast.makeText(getContext(),"Device identifier must be 6 characters",Toast.LENGTH_SHORT).show();
                } else {
                    mainVehicle.setDevice(selectedWheel,deviceID.getText().toString().trim());
                    mPairViewModel.update(mainVehicle);
                    PairDeviceManualDialog.this.getDialog().cancel();
                    pairDeviceDialog.getDialog().cancel();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PairDeviceManualDialog.this.getDialog().cancel();
            }
        });

        builder.setView(root);

        return builder.create();
    }
}
