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

    //TIME DOMAIN
    public double getNLastSDNN(int n){ //diferença atual e média
        // A high-risk group may be selected by the dichotomy limits of SDNN <50 ms
        ArrayList<Integer> rr = this.rr;
        double SDNN= 0;
        if(rr.size()>n){
            Float sum = (float) 0;
            for (int i = rr.size(); i < (rr.size() - n); i--) {
                sum = sum + rr.get(i);
            }
            float media = sum/(rr.size()+1);
            double diff = 0;
            for (int i = rr.size(); i < (rr.size() - n); i--) {
                diff = diff + Math.pow((double) rr.get(i) - media, 2);
            }
            SDNN = Math.sqrt((diff/(rr.size()-1)));
        }
        return SDNN;}

    public double getPNN50(){ //percentagem de intervalos seguidos com diferença maior que 50ms
        ArrayList<Integer> rr = this.rr;
        double PNN50 = 0;
        if(rr.size()>0){
            for (int i = 0; i < rr.size(); i++) {
                if ((rr.get(i+1) - rr.get(i)) > 50) {
                    PNN50 += 1;
                }
            }
        }
        PNN50 = PNN50*100/(rr.size()-1);
        return PNN50;}
    public double getRMSSD(){ //diferença entre atual e anterior
        ArrayList<Integer> rr = this.rr;
        double RMSSD = 0;
        if(rr.size()>0){
            double diff = 0;
            for (int i = 0; i < (rr.size()); i++) {
                diff = diff + Math.pow((double) rr.get(i+1) - rr.get(i), 2);
            }
            RMSSD = Math.sqrt((diff/(rr.size()-1)));
        }
        return RMSSD;}

    public String getStatus(User user){
        String status = new String("Normal");
        String sex = user.getSex();
        int age = user.getAge();

        return status;}

    /*private void getStress(){
        if(this.rr.get(this.rr.size()-1)<NormalValues10Sec()){
            System.out.printf("STRESSED");
        }
    }*/

    private void NormalValues10Sec(){
        //SDNN: 24.1±16.4
        //RMSSD: 27.3±22.2
        // https://www.ncbi.nlm.nih.gov/pmc/articles/PMC5010946/
    }

    private void NormalValues5min(String sex, int age){
        //SDNN: 141+-39 ms
        //SDANN: 127+-35 ms
        //RMSSD: 27+- 12ms -> WELLTORY: 19 – 48 ms — healthy adults in the age group of 38 – 42 years
        //35 – 107 ms — elite athletes

        /*Freq Domain:
        tp: 3466+-1018 ms^2
        LF: 1170+-416 ms^2
        HF:975+-203 ms^2
        LF/HF: 1.5-2-0

        LOW HRV MORTALITY:
        rmssd<15ms
        pnn50:<0.75%
        SDNN<50ms
         */
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

}
