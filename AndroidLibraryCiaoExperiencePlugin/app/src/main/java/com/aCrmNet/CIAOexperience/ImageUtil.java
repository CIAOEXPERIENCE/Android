package com.aCrmNet.CIAOexperience;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

/**
 * Created by Andrea on 12/03/2017.
 */

public class ImageUtil {
    private Context context;
    AdapterListView.OnImageLoad imageLoad;
    ImageUtil(Context context){
        this.context = context;
    }
    public void setImageLoadListener(AdapterListView.OnImageLoad imageLoad){
        this.imageLoad = imageLoad;
    }
    public void setImageRatio(String img, ImageView view, final int targetWidth){
        Transformation transformation = new Transformation() {
            @Override
            public Bitmap transform(Bitmap source) {
                 int targetWidth = Constant.getScreenWidth();
                double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                int targetHeight = (int) (targetWidth * aspectRatio);
                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                if (result != source) {
                    // Same bitmap is returned if sizes are the same
                    source.recycle();
                }
                return result;
            }
            @Override
            public String key() {
                return "transformation" + " desiredWidth";
            }
        };
        if(img!=null&& !img.equals(""))
        Picasso.with(context)
                .load(img)
                .placeholder(R.drawable.placeholder)
                .transform(transformation)
                .into(view, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.i("PICASSO SUCCESS","adapter success");
                        if(imageLoad!=null)
                            imageLoad.ImageLoad();
                    }
                    @Override
                    public void onError() {
                        Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void setCatIcon(ImageView view, String idCat){
        switch (idCat){
            case "1":
                view.setImageResource(R.drawable.restaurants_drinks);
                break;
            case "2":
                view.setImageResource(R.drawable.clubs);
                break;
            case "3":
                view.setImageResource(R.drawable.clubs);
                break;
            case "4":
                view.setImageResource(R.drawable.shopping_experience);
                break;
            case "5":
                view.setImageResource(R.drawable.attractions);
                break;
            case "6":
                view.setImageResource(R.drawable.arts);
                break;
            case "7":
                view.setImageResource(R.drawable.wellness_spa);
                break;
            case "8":
                view.setImageResource(R.drawable.extracities);
                break;
            case "9":
                view.setImageResource(R.drawable.hotels);
                break;
            case "10":
                view.setImageResource(R.drawable.events);
                break;
            case "11":
                view.setImageResource(R.drawable.services);
                break;
        }
    }
}
