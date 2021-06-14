package com.bletpms.app.ui.pair;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bletpms.app.MainActivity;
import com.bletpms.app.R;
import com.bletpms.app.bluetooth.BluetoothService;
import com.bletpms.app.database.Vehicle;
import com.bletpms.app.utils.BitmapFromAssetsProvider;
import com.bletpms.app.utils.VehicleTypes;
import com.bletpms.app.viewmodels.PairViewModel;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class PairFragment extends Fragment {

    private PairViewModel pairViewModel;
    private View root;
    private ArrayList<MaterialCardView> cards;

    private boolean layoutLoaded = false;

    private BluetoothService bluetoothService;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        pairViewModel =
                new ViewModelProvider(this).get(PairViewModel.class);
        root = inflater.inflate(R.layout.fragment_pair,container, false);

        bluetoothService = ((MainActivity)requireActivity()).getBluetoothService();

        pairViewModel.getMainVehicle().observe(getViewLifecycleOwner(), vehicle -> {
            Log.i("MAIN VEHICLE","modified!");
            if (!layoutLoaded) loadImageAndLayout(vehicle, inflater);

            for (MaterialCardView card:cards) {
                int wheelNumber = cards.indexOf(card)+1;
                TextView textView = card.findViewById(R.id.pairTextView);
                String deviceID = vehicle.getDevice(wheelNumber);
                if (deviceID != null){
                    textView.setText(deviceID);
                }else {
                    textView.setText(R.string.pair_bind);
                }
            }
        });

        return root;
    }

    private  void loadImageAndLayout(Vehicle vehicle, LayoutInflater inflater){
        String layoutName = VehicleTypes.getAllLayouts().get(vehicle.getType());
        int layoutId = getResources().getIdentifier(layoutName, "layout", "com.bletpms.app");

        LinearLayout baseLayout = root.findViewById(R.id.pairView);
        View newRoot = inflater.inflate(layoutId, (ViewGroup) root, false);
        baseLayout.addView(newRoot);

        final ImageView vehicleImage = newRoot.findViewById(R.id.vehicleImageView);
        vehicleImage.setImageBitmap(new BitmapFromAssetsProvider(getContext()).getBitmap(vehicle.getType()));

        cards = new ArrayList<>();
        for (int i = 0; i < vehicle.getWheels(); i++ ){
            String cardIDString = "card"+(i+1);
            int cardID = getResources().getIdentifier(cardIDString, "id","com.bletpms.app");
            cards.add(root.findViewById(cardID));
        }

        createCardsContent(vehicle, inflater);

        layoutLoaded = true;
    }

    private void createCardsContent(Vehicle vehicle, LayoutInflater inflater) {
        for (MaterialCardView card: cards) {
            View newRoot = inflater.inflate(R.layout.card_pair, (ViewGroup) root, false);
            card.addView(newRoot);

            card.setOnClickListener(v -> {
                DialogFragment pairDeviceFragment = new PairDeviceDialog(vehicle, pairViewModel, cards.indexOf(card)+1, card, bluetoothService);
                pairDeviceFragment.show(((AppCompatActivity)requireContext()).getSupportFragmentManager(), "Pair device");
            });
        }
    }

}