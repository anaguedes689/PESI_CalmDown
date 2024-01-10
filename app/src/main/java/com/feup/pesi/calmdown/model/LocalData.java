package com.feup.pesi.calmdown.model;

import java.util.Date;

public class LocalData {
    private int nleads;
    private long position;
    private int rr;
    private int bpmi;
    private int bpm;
    private Date dateTimeSpan;
    private int bytes;
    private String address;
    private int batteryLevel;
    private int pulse;
    private String userId;


    public LocalData() {
    }

    public LocalData(int nleads, long position, int rr, int bpmi, int bpm, Date dateTimeSpan, int bytes, String address, int batteryLevel, int pulse, String userId) {
        this.nleads = nleads;
        this.position = position;
        this.rr = rr;
        this.bpmi = bpmi;
        this.bpm = bpm;
        this.dateTimeSpan = dateTimeSpan;
        this.bytes = bytes;
        this.address = address;
        this.batteryLevel = batteryLevel;
        this.pulse = pulse;
        this.userId = userId;
    }

    public int getNleads() {
        return nleads;
    }

    public void setNleads(int nleads) {
        this.nleads = nleads;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public int getRr() {
        return rr;
    }

    public void setRr(int rr) {
        this.rr = rr;
    }

    public int getBpmi() {
        return bpmi;
    }

    public void setBpmi(int bpmi) {
        this.bpmi = bpmi;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public Date getDateTimeSpan() {
        return dateTimeSpan;
    }

    public void setDateTimeSpan(Date dateTimeSpan) {
        this.dateTimeSpan = dateTimeSpan;
    }

    public int getBytes() {
        return bytes;
    }

    public void setBytes(int bytes) {
        this.bytes = bytes;
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

    public int getPulse() {
        return pulse;
    }

    public void setPulse(int pulse) {
        this.pulse = pulse;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
