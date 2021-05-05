package com.bletpms.app.ui.vehicles.newVehicle;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bletpms.app.R;
import com.bletpms.app.database.Vehicle;
import com.bletpms.app.ui.vehicles.VehiclesViewModel;
import com.bletpms.app.utils.VehicleTypes;

import java.util.ArrayList;

public class NewVehicleDialog extends DialogFragment {

    private VehiclesViewModel mVehiclesViewModel;
    private NewVehicleAdapter adapter;
    private ArrayList<String> images = new ArrayList<>();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root =inflater.inflate(R.layout.dialog_new_vehicle, null);

        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewNewVehicle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new NewVehicleAdapter(getContext(), images);
        recyclerView.setAdapter(adapter);

        createVehicleTypesList();

        EditText vehicleName = root.findViewById(R.id.deviceIdEditText);
        Button saveButton = root.findViewById(R.id.pairDeviceSaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vehicleName.getText().toString().matches("")){
                    Toast.makeText(getContext(),"Please, enter a vehicle name",Toast.LENGTH_SHORT).show();
                } else {
                    if (adapter.getSelected() == null){
                        Toast.makeText(getContext(),"Please, select vehicle type",Toast.LENGTH_SHORT).show();
                    } else {
                        String newVehicleName = vehicleName.getText().toString();
                        //Toast.makeText(getContext(),"Vehicle created!! Name: " + newVehicleName + ", Type: " + adapter.getSelected() ,Toast.LENGTH_SHORT).show();
                        String imageName = adapter.getSelected();
                        String vehicleType = imageName.substring(0, imageName.length() - 4);
                        int numberWheels = VehicleTypes.getAllWheels().get(vehicleType);
                        Vehicle vehicle = new Vehicle(newVehicleName, vehicleType, numberWheels);
                        mVehiclesViewModel = new ViewModelProvider(requireParentFragment()).get(VehiclesViewModel.class);
                        mVehiclesViewModel.insert(vehicle);
                        NewVehicleDialog.this.getDialog().cancel();
                    }
                }
            }
        });

        builder.setView(root);

        return builder.create();
    }

    private void createVehicleTypesList(){
        images = VehicleTypes.getImagesName();
        adapter.setImages(images);
    }
}
