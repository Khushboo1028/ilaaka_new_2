package com.replon.www.replonhomy.Messaging.Model;

public class GroupChat {

    private String sender;
    private String message;
    private boolean isSeen;
    private long time_sent;
    private String image_url;
    private String pdf_url;
    private String sender_flat;

    public GroupChat(String sender, String message, boolean isSeen, long time_sent, String image_url, String pdf_url, String sender_flat) {
        this.sender = sender;
        this.message = message;
        this.isSeen = isSeen;
        this.time_sent = time_sent;
        this.image_url = image_url;
        this.pdf_url = pdf_url;
        this.sender_flat = sender_flat;
    }

    public GroupChat(){}

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public long getTime_sent() {
        return time_sent;
    }

    public void setTime_sent(long time_sent) {
        this.time_sent = time_sent;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getPdf_url() {
        return pdf_url;
    }

    public void setPdf_url(String pdf_url) {
        this.pdf_url = pdf_url;
    }

    public String getSender_flat() {
        return sender_flat;
    }

    public void setSender_flat(String sender_flat) {
        this.sender_flat = sender_flat;
    }
}
