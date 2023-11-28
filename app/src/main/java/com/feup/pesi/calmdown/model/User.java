package com.feup.pesi.calmdown.model;

import java.util.Date;

public class User {
    String name;
    Date birthdaydate;
    String userEmail;
    String password;
    int height;
    int weight;

    Quizz quizz;
    JacketData jacketData;

    public User(String name, Date birthdaydate, String userEmail, String password, int height, int weight, Quizz quizz, JacketData jacketData) {
        this.name = name;
        this.birthdaydate = birthdaydate;
        this.userEmail = userEmail;
        this.password = password;
        this.height = height;
        this.weight = weight;
        this.quizz = quizz;
        this.jacketData = jacketData;
    }

    public User(String name, Date birthdaydate, String userEmail, String password, int height, int weight, Quizz quizz) {
        this.name = name;
        this.birthdaydate = birthdaydate;
        this.userEmail = userEmail;
        this.password = password;
        this.height = height;
        this.weight = weight;
        this.quizz = quizz;
    }

    public User(String name, Date birthdaydate, String userEmail, String password, int height, int weight) {
        this.name = name;
        this.birthdaydate = birthdaydate;
        this.userEmail = userEmail;
        this.password = password;
        this.height = height;
        this.weight = weight;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Quizz getQuizz() {
        return quizz;
    }

    public void setQuizz(Quizz quizz) {
        this.quizz = quizz;
    }

    public JacketData getJacketData() {
        return jacketData;
    }

    public void setJacketData(JacketData jacketData) {
        this.jacketData = jacketData;
    }
}
