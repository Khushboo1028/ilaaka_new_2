package com.replon.www.replonhomy.Nearby;


public class ContentsNearbyPlaces {

    private String place_name, place_address, place_phno, place_rating, place_id;

    public ContentsNearbyPlaces(String place_name, String place_address, String place_phno, String place_rating, String place_id) {
        this.place_name = place_name;
        this.place_address = place_address;
        this.place_phno = place_phno;
        this.place_rating = place_rating;
        this.place_id = place_id;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getPlace_address() {
        return place_address;
    }

    public void setPlace_address(String place_address) {
        this.place_address = place_address;
    }

    public String getPlace_phno() {
        return place_phno;
    }

    public void setPlace_phno(String place_phno) {
        this.place_phno = place_phno;
    }

    public String getPlace_rating() {
        return place_rating;
    }

    public void setPlace_rating(String place_rating) {
        this.place_rating = place_rating;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }
}
