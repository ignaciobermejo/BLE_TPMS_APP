package com.bletpms.app.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

public class BitmapFromAssetsProvider {

    private final Context context;
    private final String imageName;

    public BitmapFromAssetsProvider(Context context, String imageName) {
        this.context = context;
        this.imageName = imageName;
    }

    public Bitmap getBitmap(){
        try {
            AssetManager assetManager = context.getAssets();
            InputStream is = assetManager.open("vehicles/" + imageName + ".png");
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
