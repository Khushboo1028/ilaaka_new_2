package com.replon.www.replonhomy.MinutesOfMeetings;

public class ContentsAddMoM {

//    private boolean addlist;
//
//    public ContentsAddMoM(boolean addlist) {
//        this.addlist = addlist;
//    }
//
//    public boolean isAddlist() {
//        return addlist;
//    }
//
//    public void setAddlist(boolean addlist) {
//        this.addlist = addlist;
//    }

    private String subject,description;

    public ContentsAddMoM(String subject, String description) {
        this.subject = subject;
        this.description = description;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
