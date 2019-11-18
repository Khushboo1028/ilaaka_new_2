package com.replon.www.replonhomy.Messaging.Model;

import java.util.Comparator;

public class User {

    private String id;
    private String email;
    private String imageURL;
    private String status;
    private String wing;
    private String flatno;
    private String name;
    private String phoneno;
    private long last_message_time;
    private  Boolean isSeen;


    public User(String id, String email, String imageURL, String status, String wing, String flatno, String name, String phoneno,long last_message_time,Boolean isSeen) {
        this.id = id;
        this.email = email;
        this.imageURL = imageURL;
        this.status = status;
        this.wing = wing;
        this.flatno = flatno;
        this.name = name;
        this.phoneno = phoneno;
        this.last_message_time=last_message_time;
        this.isSeen=isSeen;
    }

    public User(){

    }

    public Boolean getSeen() {
        return isSeen;
    }

    public void setSeen(Boolean seen) {
        isSeen = seen;
    }

    public long getLast_message_time() {
        return last_message_time;
    }

    public void setLast_message_time(long last_message_time) {
        this.last_message_time = last_message_time;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getWing() {
        return wing;
    }

    public void setWing(String wing) {
        this.wing = wing;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }



    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getFlatno() {
        return flatno;
    }

    public void setFlatno(String flatno) {
        this.flatno = flatno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Comparator<User> timeCompare=new Comparator<User>() {
        @Override
        public int compare(User o1, User o2) {
            return (o2.getLast_message_time()<o1.getLast_message_time() ? -1:
                    (o2.getLast_message_time()==o1.getLast_message_time() ? 0 : 1));
        }
    };

    public static Comparator<User> flatCompare=new Comparator<User>() {
        @Override
        public int compare(User o1, User o2) {
            return (o2.getFlatno().compareTo(o1.getFlatno()));
        }
    };



}
