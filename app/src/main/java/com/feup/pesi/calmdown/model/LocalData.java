package com.feup.pesi.calmdown.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LocalData {
    private String address;
    private int batteryLevel;
    private int bpm;
    private int bpmi;
    private int nBytes;
    private int nleads;
    private int position;
    private int pulse;
    private int rr;
    private Date dateTimeSpan;
    private String userId;

    // Constructors, getters, and setters


    public LocalData(String address, int batteryLevel, int bpm, int bpmi, int nBytes, int nleads, int position, int pulse, int rr, Date dateTimeSpan, String userId) {
        this.address = address;
        this.batteryLevel = batteryLevel;
        this.bpm = bpm;
        this.bpmi = bpmi;
        this.nBytes = nBytes;
        this.nleads = nleads;
        this.position = position;
        this.pulse = pulse;
        this.rr = rr;
        this.dateTimeSpan = dateTimeSpan;
        this.userId = userId;
    }

    public LocalData() {

    }

    public Date getDateTimeSpan() {
        return dateTimeSpan;
    }

    public void setDateTimeSpan(Date dateTimeSpan) {
        this.dateTimeSpan = dateTimeSpan;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public int getnBytes() {
        return nBytes;
    }

    public void setnBytes(int nBytes) {
        this.nBytes = nBytes;
    }

    public int getNleads() {
        return nleads;
    }

    public void setNleads(int nleads) {
        this.nleads = nleads;
    }

    public int getBpmi() {
        return bpmi;
    }

    public void setBpmi(int bpmi) {
        this.bpmi = bpmi;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPulse() {
        return pulse;
    }

    public void setPulse(int pulse) {
        this.pulse = pulse;
    }

    public int getRr() {
        return rr;
    }

    public void setRr(int rr) {
        this.rr = rr;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}