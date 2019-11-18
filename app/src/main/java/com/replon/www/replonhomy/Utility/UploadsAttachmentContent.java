package com.replon.www.replonhomy.Utility;


import android.graphics.Bitmap;

public class UploadsAttachmentContent {

    private Bitmap image;


    public UploadsAttachmentContent(Bitmap image) {
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}