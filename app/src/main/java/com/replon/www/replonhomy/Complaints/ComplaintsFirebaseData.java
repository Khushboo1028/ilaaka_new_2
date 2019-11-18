package com.replon.www.replonhomy.Complaints;
import java.io.Serializable;
import com.google.type.Date;

public class ComplaintsFirebaseData {

    Date date;
    String description;
    String imageURL;
    String solved;
    String subject;
    String userId;


    public ComplaintsFirebaseData(Date date, String description, String imageURL, String solved, String subject, String userId) {
        this.date = date;
        this.description = description;
        this.imageURL = imageURL;
        this.solved = solved;
        this.subject = subject;
        this.userId = userId;
    }

    public ComplaintsFirebaseData() {

    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSolved() {
        return solved;
    }

    public void setSolved(String solved) {
        this.solved = solved;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
