package com.replon.www.replonhomy.Messaging.Model;

public class Chat {

    private String Sender;
    private String receiver;
    private String message;
    private boolean isSeen;
    private long time_sent;
    private String image_url;
    private String pdf_url;


    public Chat(String sender, String receiver, String message, boolean isSeen, long time_sent, String image_url, String pdf_url) {
        this.Sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isSeen = isSeen;
        this.time_sent=time_sent;
        this.image_url=image_url;
        this.pdf_url=pdf_url;
    }
    public Chat(){

    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public String getPdf_url() {
        return pdf_url;
    }

    public void setPdf_url(String pdf_url) {
        this.pdf_url = pdf_url;
    }


    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public long getTime_sent() {
        return time_sent;
    }

    public void setTime_sent(long time_sent) {
        this.time_sent = time_sent;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        Sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
