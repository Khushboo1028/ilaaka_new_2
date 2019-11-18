package com.replon.www.replonhomy.NoticeBoard;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ContentsNotice {
    String subject,description,date_in_view_format;
    String month,day,year,time;
    private ArrayList<String> fb_imageURl,fb_pdfURL;
    DocumentReference document_id;


    public ContentsNotice(String subject, String month, String day, String year,String description,String date_in_view_format,ArrayList<String>fb_imageURl
    ,DocumentReference document_id,String time,ArrayList<String> fb_pdfURL) {
        this.subject = subject;
        this.month = month;
        this.day = day;
        this.year = year;
        this.description=description;
        this.date_in_view_format=date_in_view_format;
        this.fb_imageURl=fb_imageURl;
        this.document_id=document_id;
        this.time=time;
        this.fb_pdfURL=fb_pdfURL;

    }

    public ArrayList<String> getFb_pdfURL() {
        return fb_pdfURL;
    }

    public void setFb_pdfURL(ArrayList<String> fb_pdfURL) {
        this.fb_pdfURL = fb_pdfURL;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ContentsNotice(){}

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

    public String getDate_in_view_format() {
        return date_in_view_format;
    }

    public void setDate_in_view_format(String date_in_view_format) {
        this.date_in_view_format = date_in_view_format;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
