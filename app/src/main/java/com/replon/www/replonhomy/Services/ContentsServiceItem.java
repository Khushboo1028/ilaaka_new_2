package com.replon.www.replonhomy.Services;

import android.media.Image;

import com.google.firebase.firestore.DocumentReference;

public class ContentsServiceItem {

    private String name;
    private String contact_number;
    private String aadhar_number;

    private String service_title;
    private String imageURL;
    private DocumentReference document_id;

    public ContentsServiceItem(String name, String contact_number, String aadhar_number,String service_title,String imageURL,DocumentReference document_id) {
        this.name = name;
        this.contact_number = contact_number;
        this.aadhar_number = aadhar_number;
        this.service_title = service_title;
        this.imageURL = imageURL;
        this.document_id = document_id;

    }

    public DocumentReference getDocument_id() {
        return document_id;
    }

    public void setDocument_id(DocumentReference document_id) {
        this.document_id = document_id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getService_title() {
        return service_title;
    }

    public void setService_title(String service_title) {
        this.service_title = service_title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getAadhar_number() {
        return aadhar_number;
    }

    public void setAadhar_number(String aadhar_number) {
        this.aadhar_number = aadhar_number;
    }
}
