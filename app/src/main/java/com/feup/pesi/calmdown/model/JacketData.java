package com.feup.pesi.calmdown.model;

import java.util.ArrayList;
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
    private ArrayList<Float> RRhistory;


    public JacketData(int batteryLevel, int pulse, Date dateTimePushButton, Date dateTimeRTC, Date dateTimeTimeSpan, int sdCardState, int numOfPushButton, BioLib.DataACC dataACC, String deviceId, String firmwareVersion, byte accSensibility, byte typeRadioEvent, byte[] infoRadioEvent, short countEvent, byte[][] ecg, int nBytes, BioLib.QRS qrs, ArrayList<Float> RRhistory) {
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
        this.RRhistory = RRhistory;
    }

    public ArrayList<Float> getRRhistory() {
        return RRhistory;
    }

    public void setRRhistory(ArrayList<Float> RRhistory) {
        this.RRhistory = RRhistory;
    }

    public void addRRhistory(ArrayList<Float> RRhistory){
        RRhistory.add((float) getQrs().rr);
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

    private void NormalValuesShortTerm(String sex, int age){
        String newage = Integer.toString(age);
        char idad = 0;
        double SDNN = 0;
        double RMSSD = 0;
        double PNN50 =0 ;
        if(newage.length()>1){ //valores normais de 10 em 10 anos
            idad = newage.charAt(0);
            if(idad>=6){
                idad = 6;
            }
        }else{
            idad = '0';
        }
        int idade = Character.getNumericValue(idad);
        if(sex.equals('f')){
            switch (idade){
                case(3):
                    SDNN = 138;
                    PNN50 = 13;
                case(4):
                    SDNN = 129;
                    PNN50 = 9;
                case(5):
                    SDNN = 135;
                    PNN50 = 5;
                case(6):
                    SDNN = 115;
                    PNN50 = 5;
                default:
                    SDNN = 188;
                    PNN50 = 23;

            }
        }
        if(sex.equals('m')){
            switch (idade){
                case(3):
                    SDNN = 165;
                    PNN50 = 13;
                case(4):
                    SDNN = 155;
                    PNN50 = 8;
                case(5):
                    SDNN = 152;
                    PNN50 = 7;
                case(6):
                    SDNN = 140;
                    PNN50 = 7;
                default:
                    SDNN = 188;
                    PNN50 = 23;
            }
        }
    }

    public void setnBytes(int nBytes) {
        this.nBytes = nBytes;
    }

    public double getNLastSDNN(int n){ //diferença atual e média
        Float[] rr = getRRhistory().toArray(new Float[0]);
        double SDNN= 0;
        if(rr.length>n){
            Float sum = (float) 0;
            for (int i = rr.length; i < (rr.length - n); i--) {
                sum = sum + rr[i];
            }
            float media = sum/(rr.length+1);
            double diff = 0;
            for (int i = rr.length; i < (rr.length - n); i--) {
                diff = diff + Math.pow((double) rr[i] - media, 2);
            }
            SDNN = Math.sqrt((diff/(rr.length-1)));
        }
    return SDNN;}

    public double getPNN50(){ //percentagem de intervalos seguidos com diferença maior que 50ms
        Float[] rr = getRRhistory().toArray(new Float[0]);
        double PNN50 = 0;
        if(rr.length>0){
            for (int i = 0; i < rr.length; i++) {
                if ((rr[i + 1] - rr[i]) > 50) {
                    PNN50 += 1;
                }
            }
        }
        PNN50 = PNN50*100/(rr.length-1);
    return PNN50;}

    public double getRMSSD(){ //diferença entre atual e anterior
        Float[] rr = getRRhistory().toArray(new Float[0]);
        double RMSSD = 0;
        if(rr.length>0){
            double diff = 0;
            for (int i = 0; i < (rr.length); i++) {
                diff = diff + Math.pow((double) rr[i+1] - rr[i], 2);
            }
            RMSSD = Math.sqrt((diff/(rr.length-1)));
        }
    return RMSSD;}

    public String getStatus(User user){
        String status = new String("Normal");
        String sex = user.getSex();
        int age = user.getAge();


    return status;}


}
