package com.bletpms.app.ui.vehicles;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bletpms.app.R;
import com.bletpms.app.database.Vehicle;
import com.bletpms.app.ui.vehicles.editVehicle.EditVehicleDialog;
import com.bletpms.app.ui.vehicles.newVehicle.NewVehicleDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VehiclesListAdapter extends RecyclerView.Adapter<VehiclesListAdapter.VehicleViewHolder> {

    private Context context;
    private List<Vehicle> mVehicles;
    private  VehiclesViewModel vehiclesViewModel;

    private int counter = 0;

    private boolean multiSelected = false;
    private boolean multiSelect = false;
    private List<Vehicle> selectedVehicles = new ArrayList<>();
    private ActionMode actionMode = null;
    private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    for (Vehicle v: selectedVehicles) {
                        vehiclesViewModel.delete(v);
                    }
                    actionMode.finish();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    actionMode.finish();
                    break;
            }
        }
    };
    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            actionMode = mode;
            multiSelect = true;
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.contextual_menu, menu);
            counter = 0;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }


        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete_item_contextual_menu:

                    Log.i("SELECTED", Arrays.toString(selectedVehicles.toArray()));
                    Log.i("MAIN", vehiclesViewModel.getMainVehicle().getValue().toString());


                    if (isMainSelected()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Main vehicle cannot be deleted").setPositiveButton(R.string.ok, null).show();
                        mode.finish();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Delete " + selectedVehicles.size()+ " vehicles?").setPositiveButton(R.string.ok, dialogClickListener)
                                .setNegativeButton(R.string.cancel, dialogClickListener).show();
                    }

                    return true;
                case R.id.edit_item_contextual_menu:
                    DialogFragment editFragment = new EditVehicleDialog(selectedVehicles.get(0), vehiclesViewModel);
                    editFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "Edit vehicle");
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.main_item_contextual_menu:
                    Vehicle selected = selectedVehicles.get(0);
                    if (!selected.getMain()){
                        vehiclesViewModel.setMain(selected);
                    }
                    mode.finish();
                    return true;
                default:
                    return false;
            }

        }

        private boolean isMainSelected() {
            Vehicle mainVehicle = vehiclesViewModel.getMainVehicle().getValue();
            for (Vehicle v: selectedVehicles) {
                if (v.getId() == mainVehicle.getId()) return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            multiSelect = false;
            selectedVehicles.clear();
            notifyDataSetChanged();
            actionMode = null;
        }
    };

    public ActionMode getActionMode() {
        return actionMode;
    }

    public VehiclesListAdapter(Context context, List<Vehicle> vehicles, VehiclesViewModel model)
    {
        this.context = context;
        this.mVehicles = vehicles;
        this.vehiclesViewModel = model;
    }

    void setVehicles(List<Vehicle> vehicles){
        this.mVehicles = new ArrayList<>();
        this.mVehicles = vehicles;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(context).inflate(R.layout.vehicle_item, parent, false);
        return new VehicleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {

        holder.bind(mVehicles.get(position));
    }

    @Override
    public int getItemCount() {
        if (mVehicles != null)
            return mVehicles.size();
        else
            return 0;
    }

    class VehicleViewHolder extends RecyclerView.ViewHolder{

        private TextView textView;
        private ImageView imageView;
        private ImageView mainImageView;
        private ConstraintLayout itemLayout;

        private VehicleViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.vehicleTextView);
            imageView = itemView.findViewById(R.id.checkedImageView);
            mainImageView = itemView.findViewById(R.id.mainImageView);
            itemLayout = itemView.findViewById(R.id.vehicleItemLinearLayout);

        }

        void selectItem(Vehicle vehicle) {
            if (multiSelect) {
                if (selectedVehicles.contains(vehicle)) {
                    selectedVehicles.remove(vehicle);
                    itemLayout.setBackgroundResource(R.color.vehicles_list_item);
                    imageView.setVisibility(View.GONE);
                    counter--;
                } else {
                    selectedVehicles.add(vehicle);
                    itemLayout.setBackgroundResource(R.color.vehicles_list_item_selected);
                    imageView.setVisibility(View.VISIBLE);
                    counter++;
                }
                actionMode.setTitle(counter + " selected");
            }
        }

        void bind(final Vehicle vehicle) {
            textView.setText(vehicle.getName());

            mainImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedVehicles.isEmpty() && !vehicle.getMain()){
                        Log.i("MAIN", vehicle.getName());
                        vehiclesViewModel.setMain(vehicle);
                        notifyDataSetChanged();
                    }
                }
            });
            if (vehicle.getMain()){
                //mainImageView.setImageResource(R.drawable.ic_baseline_star_24);
                mainImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_star_24));
                //mainImageView.setColorFilter(ContextCompat.getColor(context,R.color.amber_600),PorterDuff.Mode.SRC_IN);
            }else {
                //mainImageView.setImageResource(R.drawable.ic_baseline_star_outline_24);
                mainImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_star_outline_24));
                //mainImageView.clearColorFilter();
            }

            if (selectedVehicles.contains(vehicle)){
                itemLayout.setBackgroundResource(R.color.vehicles_list_item_selected);
                imageView.setVisibility(View.VISIBLE);
            } else {
                itemLayout.setBackgroundResource(R.color.vehicles_list_item);
                imageView.setVisibility(View.GONE);
            }

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (selectedVehicles.isEmpty()){
                        ((AppCompatActivity)v.getContext()).startSupportActionMode(actionModeCallbacks);
                        selectItem(vehicle);
                        return true;
                    }
                    return false;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectItem(vehicle);

                    if (selectedVehicles.size() > 1){
                        if (!multiSelected){
                            actionMode.getMenu().setGroupVisible(R.id.single_selection_contextual_menu_group, false);
                            multiSelected = true;
                        }
                    } else if (actionMode != null){
                        if (selectedVehicles.isEmpty()) actionMode.finish();
                        else {
                            actionMode.getMenu().setGroupVisible(R.id.single_selection_contextual_menu_group, true);
                            multiSelected = false;
                        }
                    }
                }
            });
        }
    }
}

