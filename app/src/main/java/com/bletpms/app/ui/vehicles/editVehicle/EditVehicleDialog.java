package com.bletpms.app.ui.vehicles.editVehicle;

import android.app.AlertDialog;
import android.app.Dialog;
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
import com.bletpms.app.ui.vehicles.VehiclesViewModel;
import com.bletpms.app.ui.vehicles.newVehicle.NewVehicleAdapter;

public class EditVehicleDialog extends DialogFragment {

    private final VehiclesViewModel mVehiclesViewModel;
    private NewVehicleAdapter adapter;
    private final Vehicle selectedVehicle;

    public EditVehicleDialog(Vehicle vehicle, VehiclesViewModel model) {
        this.selectedVehicle = vehicle;
        this.mVehiclesViewModel = model;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root =inflater.inflate(R.layout.dialog_edit_vehicle, null);

        EditText vehicleName = root.findViewById(R.id.deviceIdEditText);
        vehicleName.setText(selectedVehicle.getName());
        vehicleName.requestFocus();
        Button saveButton = root.findViewById(R.id.pairDeviceSaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vehicleName.getText().toString().matches("")){
                    Toast.makeText(getContext(),"Please, enter a vehicle name",Toast.LENGTH_SHORT).show();
                } else {
                    selectedVehicle.setName(vehicleName.getText().toString().trim());
                    mVehiclesViewModel.update(selectedVehicle);
                    EditVehicleDialog.this.getDialog().cancel();
                }
            }
        });

        builder.setView(root);

        return builder.create();
    }
}
