package com.replon.www.replonhomy.Utility;


import android.graphics.Bitmap;
import android.media.Image;
import android.widget.ImageView;

public class DownloadsAttachmentContent {

    private String imageURL,fileType,pdfURL;


    public DownloadsAttachmentContent(String imageURL, String fileType, String pdfURL) {
        this.imageURL = imageURL;
        this.fileType=fileType;
        this.pdfURL=pdfURL;
    }

    public String getPdfURL() {
        return pdfURL;
    }

    public void setPdfURL(String pdfURL) {
        this.pdfURL = pdfURL;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String image) {
        this.imageURL = imageURL;
    }


}
