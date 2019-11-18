package com.replon.www.replonhomy.Messaging.Adapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;

@GlideModule
public class MyAppGlideModule extends AppGlideModule {

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        // Register FirebaseImageLoader to handle StorageReference
        registry.append(StorageReference.class, InputStream.class,new FirebaseImageLoader.Factory());
    }

//    @NonNull
//    @GlideOption
//    public static RequestOptions roundedCorners(RequestOptions options, @NonNull Context context, int cornerRadius) {
//        int px = Math.round(cornerRadius * (context.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
//        return options.transforms(new RoundedCorners(px));
//    }
}