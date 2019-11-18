package com.replon.www.replonhomy.Utility;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;


public class UserDataFirebase {

    String flat_no;
    Timestamp date_created;
    Boolean admin;
    String Society_name;
    String username;
    String name;
    DocumentReference soc_id;  String wing;
    String unique_id;


    public UserDataFirebase(String flat_no,String name, Timestamp date_created, Boolean admin, String society_name, String username,DocumentReference soc_id,
                            String unique_id) {
        this.flat_no = flat_no;
        this.date_created = date_created;
        this.admin = admin;
        this.Society_name = society_name;
        this.username = username;
        this.name=name;
        this.soc_id=soc_id;
        this.unique_id=unique_id;

    }

    public UserDataFirebase() {

    }



    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public DocumentReference getSoc_id() {
        return soc_id;
    }

    public void setSoc_id(DocumentReference soc_id) {
        this.soc_id = soc_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlat_no() {
        return flat_no;
    }

    public void setFlat_no(String flat_no) {
        this.flat_no = flat_no;
    }

    public Timestamp getDate_created() {
        return date_created;
    }

    public void setDate_created(Timestamp date_created) {
        this.date_created = date_created;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public String getSociety_name() {
        return Society_name;
    }

    public void setSociety_name(String society_name) {
        Society_name = society_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
