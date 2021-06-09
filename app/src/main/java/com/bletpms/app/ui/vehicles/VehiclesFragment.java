package com.bletpms.app.ui.vehicles;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bletpms.app.R;
import com.bletpms.app.adapters.VehiclesListAdapter;
import com.bletpms.app.database.Vehicle;
import com.bletpms.app.ui.vehicles.newVehicle.NewVehicleDialog;
import com.bletpms.app.viewmodels.VehiclesViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class VehiclesFragment extends Fragment {

    private final List<Vehicle> vehicles = new ArrayList<>();
    private VehiclesListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        VehiclesViewModel vehiclesViewModel = new ViewModelProvider(this).get(VehiclesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_vehicles, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(),LinearLayoutManager.VERTICAL));
        adapter = new VehiclesListAdapter(getContext(), vehicles, vehiclesViewModel);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            if (adapter.getActionMode() != null){
                adapter.getActionMode().finish();
            }
            DialogFragment newFragment = new NewVehicleDialog();
            newFragment.show(getParentFragmentManager(),"New vehicle");
        });

        vehiclesViewModel.getAllVehicles().observe(getViewLifecycleOwner(), vehicles -> adapter.setVehicles(vehicles));

        vehiclesViewModel.getMainVehicle().observe(getViewLifecycleOwner(), vehicle -> {
            //Toast.makeText(getContext(),"Main vehicle: " + vehicle.getName() ,Toast.LENGTH_SHORT).show();
        });

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(adapter.getActionMode() != null)
            adapter.getActionMode().finish();
    }
}