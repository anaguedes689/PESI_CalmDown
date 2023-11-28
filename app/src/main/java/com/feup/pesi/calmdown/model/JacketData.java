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
    private short countEvent;

    private byte[][] ecg;
    private int nBytes;


    public void processBioLibOutput(int batteryLevel, int pulse, Date dateTimePushButton,
                                    Date dateTimeRTC, Date dateTimeTimeSpan, int sdCardState,
                                    int numOfPushButton, BioLib.DataACC dataACC, String deviceId,
                                    String firmwareVersion, byte accSensibility, byte typeRadioEvent,
                                    byte[] infoRadioEvent, short countEvent, byte[][] ecg, int nBytes) {

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
        this.countEvent = countEvent;
        this.ecg = ecg;
        this.nBytes = nBytes;

        // Adicione outras operações de processamento conforme necessário
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

    public short getCountEvent() {
        return countEvent;
    }

    public byte[][] getEcg() {
        return ecg;
    }

    public int getnBytes() {
        return nBytes;
    }
}
