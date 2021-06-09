package com.bletpms.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bletpms.app.R;
import com.bletpms.app.utils.BitmapFromAssetsProvider;
import com.google.android.material.card.MaterialCardView;

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
        final ImageView image;
        final MaterialCardView card;

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
            image.setImageBitmap(new BitmapFromAssetsProvider(mContext).getBitmap(imageName.substring(0,imageName.length()-4)));
            /*try {
                AssetManager assetManager = mContext.getAssets();
                InputStream is = assetManager.open("vehicles/" + imageName);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                image.setImageBitmap(bitmap);

            }catch (IOException e){
            }*/

            card.setOnClickListener(v -> {
                card.setChecked(true);
                if (checkedPosition != getAdapterPosition()){
                    notifyItemChanged(checkedPosition);
                    checkedPosition = getAdapterPosition();
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
