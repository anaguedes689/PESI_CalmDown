package com.feup.pesi.calmdown.model;

import java.util.Date;

import Bio.Library.namespace.BioLib;

public class JacketData {
    private int batteryLevel;
    private int pulse;
    private Date dateTimePushButton;
    private Date dateTimeRTC;
    private Date dateTimeTimeSpan;
    private int sdCardState;
    private int numOfPushButton;
    private BioLib.DataACC dataACC;
    private String deviceId;
    private String firmwareVersion;
    private byte accSensibility;
    private byte typeRadioEvent;
    private byte[] infoRadioEvent;
    private byte[][] ecg;
    private int nBytes;
    private BioLib.QRS qrs;


    public JacketData(int batteryLevel, int pulse, Date dateTimePushButton, Date dateTimeRTC, Date dateTimeTimeSpan, int sdCardState, int numOfPushButton, BioLib.DataACC dataACC, String deviceId, String firmwareVersion, byte accSensibility, byte typeRadioEvent, byte[] infoRadioEvent, short countEvent, byte[][] ecg, int nBytes, BioLib.QRS qrs) {
        this.batteryLevel = batteryLevel;
        this.pulse = pulse;
        this.dateTimePushButton = dateTimePushButton;
        this.dateTimeRTC = dateTimeRTC;
        this.dateTimeTimeSpan = dateTimeTimeSpan;
        this.sdCardState = sdCardState;
        this.numOfPushButton = numOfPushButton;
        this.dataACC = dataACC;
        this.deviceId = deviceId;
        this.firmwareVersion = firmwareVersion;
        this.accSensibility = accSensibility;
        this.typeRadioEvent = typeRadioEvent;
        this.infoRadioEvent = infoRadioEvent;
        this.ecg = ecg;
        this.nBytes = nBytes;
        this.qrs = qrs;
    }

    public BioLib.QRS getQrs() {
        return qrs;
    }

    public void setQrs(BioLib.QRS qrs) {
        this.qrs = qrs;
    }

    // Métodos de acesso para obter os valores dos atributos
    public int getBatteryLevel() {
        return batteryLevel;
    }

    public int getPulse() {
        return pulse;
    }

    public Date getDateTimePushButton() {
        return dateTimePushButton;
    }

    public Date getDateTimeRTC() {
        return dateTimeRTC;
    }

    public Date getDateTimeTimeSpan() {
        return dateTimeTimeSpan;
    }

    public int getSdCardState() {
        return sdCardState;
    }

    public int getNumOfPushButton() {
        return numOfPushButton;
    }

    public BioLib.DataACC getDataACC() {
        return dataACC;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public byte getAccSensibility() {
        return accSensibility;
    }

    public byte getTypeRadioEvent() {
        return typeRadioEvent;
    }

    public byte[] getInfoRadioEvent() {
        return infoRadioEvent;
    }



    public byte[][] getEcg() {
        return ecg;
    }

    public int getnBytes() {
        return nBytes;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public void setPulse(int pulse) {
        this.pulse = pulse;
    }

    public void setDateTimePushButton(Date dateTimePushButton) {
        this.dateTimePushButton = dateTimePushButton;
    }

    public void setDateTimeRTC(Date dateTimeRTC) {
        this.dateTimeRTC = dateTimeRTC;
    }

    public void setDateTimeTimeSpan(Date dateTimeTimeSpan) {
        this.dateTimeTimeSpan = dateTimeTimeSpan;
    }

    public void setSdCardState(int sdCardState) {
        this.sdCardState = sdCardState;
    }

    public void setNumOfPushButton(int numOfPushButton) {
        this.numOfPushButton = numOfPushButton;
    }

    public void setDataACC(BioLib.DataACC dataACC) {
        this.dataACC = dataACC;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public void setAccSensibility(byte accSensibility) {
        this.accSensibility = accSensibility;
    }

    public void setTypeRadioEvent(byte typeRadioEvent) {
        this.typeRadioEvent = typeRadioEvent;
    }

    public void setInfoRadioEvent(byte[] infoRadioEvent) {
        this.infoRadioEvent = infoRadioEvent;
    }


    public void setEcg(byte[][] ecg) {
        this.ecg = ecg;
    }

    public void setnBytes(int nBytes) {
        this.nBytes = nBytes;
    }
}
