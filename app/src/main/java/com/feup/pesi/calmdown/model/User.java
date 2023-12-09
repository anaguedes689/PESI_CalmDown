package com.feup.pesi.calmdown.model;

import java.util.ArrayList;
import java.util.Date;

import Bio.Library.namespace.BioLib;

public class User {
    String name;
    Date birthdaydate;
    String userEmail;
    int height;
    int weight;
    String sex;
    Quizz quizz;

    ArrayList<Date> dateTimeTimeSpan;

    public User() {
    }


    public User(String name, Date birthdaydate, String userEmail, int height, int weight, String sex, Quizz quizz) {
        this.name = name;
        this.birthdaydate = birthdaydate;
        this.userEmail = userEmail;
        this.height = height;
        this.weight = weight;
        this.quizz = quizz;
        this.sex = sex;

    }

    public User(String name, Date birthdaydate, String userEmail, int height, int weight, String sex) {
        this.name = name;
        this.birthdaydate = birthdaydate;
        this.userEmail = userEmail;
        this.height = height;
        this.weight = weight;
        this.sex = sex;
    }


    public ArrayList<Date> getDateTimeTimeSpan() {
        return dateTimeTimeSpan;
    }

    public void setDateTimeTimeSpan(ArrayList<Date> dateTimeTimeSpan) {
        this.dateTimeTimeSpan = dateTimeTimeSpan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthdaydate() {
        return birthdaydate;
    }

    public void setBirthdaydate(Date birthdaydate) {
        this.birthdaydate = birthdaydate;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Quizz getQuizz() {
        return quizz;
    }

    public void setQuizz(Quizz quizz) {
        this.quizz = quizz;
    }




    public void insertqrs(){

    }
}
