package com.replon.www.replonhomy.MinutesOfMeetings;

import java.util.ArrayList;

public class ContentsMoM {

    private String meeting_name;
    private ArrayList<String> meeting_subjects;
    private ArrayList<String> meeting_description;
    private String month;
    private String date;
    private String year;
    private String date_inViewFormat;
    private String time;
    private String time_created;
    private ArrayList<String> image_url;
    private ArrayList<String> pdf_url;



    public ContentsMoM(String meeting_name, ArrayList<String> meeting_subjects, ArrayList<String> meeting_description,
                       String month, String date, String year, String date_inViewFormat, String time,ArrayList<String> pdf_url,ArrayList<String> image_url) {
        this.meeting_name = meeting_name;
        this.meeting_subjects = meeting_subjects;
        this.meeting_description = meeting_description;
        this.month = month;
        this.date = date;
        this.year = year;
        this.date_inViewFormat = date_inViewFormat;
        this.time = time;
        this.pdf_url=pdf_url;
        this.image_url=image_url;

    }


    public ArrayList<String> getImage_url() {
        return image_url;
    }

    public void setImage_url(ArrayList<String> image_url) {
        this.image_url = image_url;
    }

    public ArrayList<String> getPdf_url() {
        return pdf_url;
    }

    public void setPdf_url(ArrayList<String> pdf_url) {
        this.pdf_url = pdf_url;
    }

    public String getTime_created() {
        return time_created;
    }

    public void setTime_created(String time_created) {
        this.time_created = time_created;
    }

    public ArrayList<String> getMeeting_subjects() {
        return meeting_subjects;
    }

    public void setMeeting_subjects(ArrayList<String> meeting_subjects) {
        this.meeting_subjects = meeting_subjects;
    }

    public ArrayList<String> getMeeting_description() {
        return meeting_description;
    }

    public void setMeeting_description(ArrayList<String> meeting_description) {
        this.meeting_description = meeting_description;
    }

    public String getDate_inViewFormat() {
        return date_inViewFormat;
    }

    public void setDate_inViewFormat(String date_inViewFormat) {
        this.date_inViewFormat = date_inViewFormat;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMeeting_name() {
        return meeting_name;
    }

    public void setMeeting_name(String meeting_name) {
        this.meeting_name = meeting_name;
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
