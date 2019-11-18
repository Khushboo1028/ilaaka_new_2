package com.replon.www.replonhomy.Settings.PersonalInfo;

public class ContentsPersonalInfo {

    String title, info;
    Boolean nxt_show;

    public ContentsPersonalInfo(String title, String info, Boolean nxt_show) {
        this.title = title;
        this.info = info;
        this.nxt_show = nxt_show;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Boolean getNxt_show() {
        return nxt_show;
    }

    public void setNxt_show(Boolean nxt_show) {
        this.nxt_show = nxt_show;
    }
}
