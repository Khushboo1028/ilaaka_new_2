package com.replon.www.replonhomy.Settings;

import android.media.Image;
import android.widget.ImageView;

public class ContentsSettings {

    private int image;
    private String setting;


    public ContentsSettings(int image, String setting) {
        this.image = image;
        this.setting = setting;

    }



    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }
}
