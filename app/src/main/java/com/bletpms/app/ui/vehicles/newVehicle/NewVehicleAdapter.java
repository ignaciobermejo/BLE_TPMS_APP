package com.bletpms.app.ui.vehicles.newVehicle;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bletpms.app.R;
import com.google.android.material.card.MaterialCardView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class NewVehicleAdapter extends RecyclerView.Adapter<NewVehicleAdapter.VehicleViewHolder>{

    private final LayoutInflater mInflater;
    private ArrayList<String> mImage;
    private final Context mContext;

    private int checkedPosition = -1;

    public NewVehicleAdapter(Context context, ArrayList<String> images) {
        this.mContext = context;
        this.mImage = images;
        mInflater = LayoutInflater.from(context);
    }

    public void setImages(ArrayList<String> images){
        this.mImage = new ArrayList<>();
        this.mImage = images;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.vehicle_type_item, parent, false);
        return new NewVehicleAdapter.VehicleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {

        holder.bind(mImage.get(position));

    }

    @Override
    public int getItemCount() {
        return mImage.size();
    }


    class VehicleViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        MaterialCardView card;

        private VehicleViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            card = itemView.findViewById(R.id.bindCard);
        }

        void bind(final String imageName){
            if (checkedPosition == -1){
                card.setChecked(false);
            } else {
                card.setChecked(checkedPosition == getAdapterPosition());
            }

            try {
                AssetManager assetManager = mContext.getAssets();
                InputStream is = assetManager.open("vehicles/" + imageName);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                image.setImageBitmap(bitmap);

            }catch (IOException e){
            }

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    card.setChecked(true);
                    if (checkedPosition != getAdapterPosition()){
                        notifyItemChanged(checkedPosition);
                        checkedPosition = getAdapterPosition();
                    }
                }
            });
        }
    }

    public String getSelected(){
        if (checkedPosition != -1){
            return mImage.get(checkedPosition);
        }
        return null;
    }
}
