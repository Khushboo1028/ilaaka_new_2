package com.replon.www.replonhomy.Voting;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;

public class ContentsPolls {



    private String poll_name;
    private String month;
    private String date;
    private String year;
    private String time_poll_ends;
    private String time_created;
    private ArrayList<String> choiceList;
    private Boolean timePassed;
    private DocumentReference doc_ref;
    private String flat_no;

    public ContentsPolls(String poll_name, String month, String date, String year, String time_poll_ends, String time_created,ArrayList<String>choiceList,
                         Boolean timePassed,DocumentReference doc_ref,String flat_no) {
        this.poll_name = poll_name;
        this.month = month;
        this.date = date;
        this.year = year;
        this.time_poll_ends = time_poll_ends;
        this.time_created = time_created;
        this.choiceList=choiceList;
        this.timePassed=timePassed;
        this.doc_ref=doc_ref;
        this.flat_no=flat_no;
    }

    public String getFlat_no() {
        return flat_no;
    }

    public void setFlat_no(String flat_no) {
        this.flat_no = flat_no;
    }

    public DocumentReference getDoc_ref() {
        return doc_ref;
    }

    public void setDoc_ref(DocumentReference doc_ref) {
        this.doc_ref = doc_ref;
    }

    public Boolean getTimePassed() {
        return timePassed;
    }

    public void setTimePassed(Boolean timePassed) {
        this.timePassed = timePassed;
    }

    public ArrayList<String> getChoiceList() {
        return choiceList;
    }

    public void setChoiceList(ArrayList<String> choiceList) {
        this.choiceList = choiceList;
    }

    public String getTime_poll_ends() {
        return time_poll_ends;
    }

    public void setTime_poll_ends(String time_poll_ends) {
        this.time_poll_ends = time_poll_ends;
    }

    public String getTime_created() {
        return time_created;
    }

    public void setTime_created(String time_created) {
        this.time_created = time_created;
    }

    public String getPoll_name() {
        return poll_name;
    }

    public void setPoll_name(String poll_name) {
        this.poll_name = poll_name;
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


}
