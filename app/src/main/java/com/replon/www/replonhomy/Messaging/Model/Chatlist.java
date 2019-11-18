package com.replon.www.replonhomy.Messaging.Model;

import android.support.annotation.NonNull;

public class Chatlist  {

    public String id;
    public long last_message_time;
    public Boolean isSeen;


    public Chatlist(String id,long last_message_time,Boolean isSeen) {

        this.id = id;
        this.last_message_time=last_message_time;
        this.isSeen=isSeen;
    }

    public Chatlist(){

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
