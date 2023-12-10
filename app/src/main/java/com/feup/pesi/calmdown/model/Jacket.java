package com.feup.pesi.calmdown.model;

import java.util.ArrayList;
import java.util.Date;

public class Jacket {
    private ArrayList<Integer> nleads;
    private ArrayList<Long> position;
    private ArrayList<Integer> rr;
    private ArrayList<Integer> bpmi;
    private ArrayList<Integer> bpm;
    private ArrayList<Date> dateTimeSpan;
    private ArrayList<Integer> nBytes;
    private String address;
    private ArrayList< Integer> batteryLevel;
    private ArrayList< Integer> pulse;
    private String userId;

    public Jacket() {
    }

    public Jacket(ArrayList<Integer> nleads, ArrayList<Long> position, ArrayList<Integer> rr, ArrayList<Integer> bpmi, ArrayList<Integer> bpm, ArrayList<Date> dateTimeSpan, ArrayList<Integer> nBytes, String address, ArrayList<Integer> batteryLevel, ArrayList<Integer> pulse, String userId) {
        this.nleads = nleads;
        this.position = position;
        this.rr = rr;
        this.bpmi = bpmi;
        this.bpm = bpm;
        this.dateTimeSpan = dateTimeSpan;
        this.nBytes = nBytes;
        this.address = address;
        this.batteryLevel = batteryLevel;
        this.pulse = pulse;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<Integer> getNleads() {
        return nleads;
    }

    public void setNleads(ArrayList<Integer> nleads) {
        this.nleads = nleads;
    }

    public ArrayList<Long> getPosition() {
        return position;
    }

    public void setPosition(ArrayList<Long> position) {
        this.position = position;
    }

    public ArrayList<Integer> getRr() {
        return rr;
    }

    public void setRr(ArrayList<Integer> rr) {
        this.rr = rr;
    }

    public ArrayList<Integer> getBpmi() {
        return bpmi;
    }

    public void setBpmi(ArrayList<Integer> bpmi) {
        this.bpmi = bpmi;
    }

    public ArrayList<Integer> getBpm() {
        return bpm;
    }

    public void setBpm(ArrayList<Integer> bpm) {
        this.bpm = bpm;
    }

    public ArrayList<Date> getDateTimeSpan() {
        return dateTimeSpan;
    }

    public void setDateTimeSpan(ArrayList<Date> dateTimeSpan) {
        this.dateTimeSpan = dateTimeSpan;
    }


    public ArrayList<Integer> getnBytes() {
        return nBytes;
    }

    public void setnBytes(ArrayList<Integer> nBytes) {
        this.nBytes = nBytes;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList<Integer> getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(ArrayList<Integer> batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public ArrayList<Integer> getPulse() {
        return pulse;
    }

    public void setPulse(ArrayList<Integer> pulse) {
        this.pulse = pulse;
    }
}
