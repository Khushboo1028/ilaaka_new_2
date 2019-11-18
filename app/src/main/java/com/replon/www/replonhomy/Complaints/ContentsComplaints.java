package com.replon.www.replonhomy.Complaints;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public class ContentsComplaints {

    private String solved_unsolved;
    private String month;
    private String date;
    private String year;
    private String subject;
    private String description;
    private String date_in_ViewFormat;
    private ArrayList<String> fb_imageURl;
    private DocumentReference document_id;
    private String flat_no;
    private String admin;
    private String user_complaint;
    private String time;


    public ContentsComplaints(String solved_unsolved, String month, String date, String year,String subject, String description,String date_in_ViewFormat,ArrayList<String> fb_imageURl,DocumentReference document_id,String flat_no,String admin,String user_complaint,String time) {
        this.solved_unsolved = solved_unsolved;
        this.month = month;
        this.date = date;
        this.year = year;
        this.description = description;
        this.subject=subject;
        this.date_in_ViewFormat=date_in_ViewFormat;
        this.fb_imageURl=fb_imageURl;
        this.document_id = document_id;
        this.flat_no=flat_no;
        this.admin=admin;
        this.user_complaint=user_complaint;
        this.time=time;

    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getUser_complaint() {
        return user_complaint;
    }

    public void setUser_complaint(String user_complaint) {
        this.user_complaint = user_complaint;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getFlat_no() {
        return flat_no;
    }

    public void setFlat_no(String flat_no) {
        this.flat_no = flat_no;
    }

    public ContentsComplaints(){}
    public DocumentReference getDocument_id() {
        return document_id;
    }

    public void setDocument_id(DocumentReference document_id) {
        this.document_id = document_id;
    }

    public ArrayList<String> getFb_imageURl() {
        return fb_imageURl;
    }

    public void setFb_imageURl(ArrayList<String> fb_imageURl) {
        this.fb_imageURl = fb_imageURl;
    }

    public String getDate_in_ViewFormat() {
        return date_in_ViewFormat;
    }

    public void setDate_in_ViewFormat(String date_in_ViewFormat) {
        this.date_in_ViewFormat = date_in_ViewFormat;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSolved_unsolved() {
        return solved_unsolved;
    }

    public void setSolved_unsolved(String solved_unsolved) {
        this.solved_unsolved = solved_unsolved;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
