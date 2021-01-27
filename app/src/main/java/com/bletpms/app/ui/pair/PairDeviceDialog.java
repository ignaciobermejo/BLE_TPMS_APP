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

public class PairDeviceDialog extends DialogFragment {

    private PairViewModel mPairViewModel;
    private Vehicle mainVehicle;
    private int selectedWheel;

    public PairDeviceDialog(Vehicle vehicle, PairViewModel model, int selectedWheel) {
        this.mainVehicle = vehicle;
        this.mPairViewModel = model;
        this.selectedWheel = selectedWheel;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root =inflater.inflate(R.layout.dialog_pair_device, null);

        EditText deviceID = root.findViewById(R.id.deviceIdEditText);
        Button saveButton = root.findViewById(R.id.pairDeviceSaveButton);
        Button unbindButton = root.findViewById(R.id.pairDeviceUnbindButton);

        if (mainVehicle.getDevice(selectedWheel) != null){
            deviceID.setText(mainVehicle.getDevice(selectedWheel));

            unbindButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Unbind device").setMessage("Are you sure?").setPositiveButton(R.string.ok, dialogClickListener)
                            .setNegativeButton(R.string.cancel, dialogClickListener).show();
                }
            });
        }else {
            unbindButton.setVisibility(View.GONE);
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
                    PairDeviceDialog.this.getDialog().cancel();
                }
            }
        });

        builder.setView(root);

        return builder.create();
    }

    private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    mainVehicle.setDevice(selectedWheel,null);
                    mPairViewModel.update(mainVehicle);
                    PairDeviceDialog.this.getDialog().cancel();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    PairDeviceDialog.this.getDialog().cancel();
                    break;
            }
        }
    };
}
