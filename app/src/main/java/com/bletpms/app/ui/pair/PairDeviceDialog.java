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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.bletpms.app.R;
import com.bletpms.app.bluetooth.BluetoothService;
import com.bletpms.app.database.Vehicle;
import com.google.android.material.card.MaterialCardView;

public class PairDeviceDialog extends DialogFragment {

    private PairViewModel mPairViewModel;
    private Vehicle mainVehicle;
    private int selectedWheel;
    private MaterialCardView card;
    private BluetoothService bluetoothService;


    public PairDeviceDialog(Vehicle vehicle, PairViewModel model, int selectedWheel, MaterialCardView card, BluetoothService bluetoothService) {
        this.mainVehicle = vehicle;
        this.mPairViewModel = model;
        this.selectedWheel = selectedWheel;
        this.card = card;
        this.bluetoothService = bluetoothService;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root =inflater.inflate(R.layout.dialog_pair_device, null);

        Button autoButton = root.findViewById(R.id.pairDeviceAutoButton);
        Button manualButton = root.findViewById(R.id.pairDeviceManualButton);
        Button unbindButton = root.findViewById(R.id.pairDeviceUnbindButton);

        autoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment pairDeviceFragment = new PairDeviceAutoDialog(mainVehicle, mPairViewModel, selectedWheel, PairDeviceDialog.this, card, bluetoothService);
                pairDeviceFragment.show(((AppCompatActivity)getContext()).getSupportFragmentManager(), "Pair device auto");
            }
        });

        manualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment pairDeviceFragment = new PairDeviceManualDialog(mainVehicle, mPairViewModel, selectedWheel, PairDeviceDialog.this);
                pairDeviceFragment.show(((AppCompatActivity)getContext()).getSupportFragmentManager(), "Pair device manual");
            }
        });

        if (mainVehicle.getDevice(selectedWheel) != null){
            unbindButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(R.string.pair_unbind_title).setMessage(R.string.pair_unbind_message).setPositiveButton(R.string.ok, dialogClickListener)
                            .setNegativeButton(R.string.cancel, dialogClickListener).show();
                }
            });
        } else {
            unbindButton.setVisibility(View.GONE);
        }

        /*saveButton.setOnClickListener(new View.OnClickListener() {
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
*/
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
