package com.replon.www.replonhomy.Emergency;

public class ContentsEmergency {

    private String emergency,emergency_number;

    public ContentsEmergency(String emergency, String emergency_number) {
        this.emergency = emergency;
        this.emergency_number = emergency_number;
    }

    public String getEmergency() {
        return emergency;
    }

    public void setEmergency(String emergency) {
        this.emergency = emergency;
    }

    public String getEmergency_number() {
        return emergency_number;
    }

    public void setEmergency_number(String emergency_number) {
        this.emergency_number = emergency_number;
    }
}
