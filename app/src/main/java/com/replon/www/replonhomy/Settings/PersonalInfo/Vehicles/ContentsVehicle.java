package com.replon.www.replonhomy.Settings.PersonalInfo.Vehicles;

import com.google.firebase.firestore.DocumentReference;

public class ContentsVehicle {


    private String img_url;
    private String vehicle_num;
    private String vehicle_type;
    private String parking_slot;
    private String vehicle_color;
    private DocumentReference document_ref;

    public ContentsVehicle(String img_url, String vehicle_num, String vehicle_type, String parking_slot, String vehicle_color,DocumentReference document_ref) {
        this.img_url = img_url;
        this.vehicle_num = vehicle_num;
        this.vehicle_type = vehicle_type;
        this.parking_slot = parking_slot;
        this.vehicle_color = vehicle_color;
        this.document_ref=document_ref;
    }


    public DocumentReference getDocument_ref() {
        return document_ref;
    }

    public void setDocument_ref(DocumentReference document_ref) {
        this.document_ref = document_ref;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getVehicle_num() {
        return vehicle_num;
    }

    public void setVehicle_num(String vehicle_num) {
        this.vehicle_num = vehicle_num;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    public String getParking_slot() {
        return parking_slot;
    }

    public void setParking_slot(String parking_slot) {
        this.parking_slot = parking_slot;
    }

    public String getVehicle_color() {
        return vehicle_color;
    }

    public void setVehicle_color(String vehicle_color) {
        this.vehicle_color = vehicle_color;
    }
}
