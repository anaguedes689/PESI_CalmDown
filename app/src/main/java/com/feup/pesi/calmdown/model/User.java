package com.feup.pesi.calmdown.model;

import java.util.Calendar;
import java.util.Date;

public class User {
    String name;
    Date birthdaydate;
    String userEmail;
    int height;
    int weight;
    String sex;
    Quizz quizz;
    JacketData jacketData;

    public User() {
    }

    public User(String name, Date birthdaydate, String userEmail, int height, int weight, String sex, Quizz quizz, JacketData jacketData) {
        this.name = name;
        this.birthdaydate = birthdaydate;
        this.userEmail = userEmail;
        this.height = height;
        this.weight = weight;
        this.sex = sex;
        this.quizz = quizz;
        this.jacketData = jacketData;
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

    public JacketData getJacketData() {
        return jacketData;
    }

    public int getAge() {
        if (this.birthdaydate == null) {
            return 0;
        }

        Calendar today = Calendar.getInstance();
        Calendar birthCalendar = Calendar.getInstance();
        birthCalendar.setTime(this.birthdaydate);

        int age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }

    public void setJacketData(JacketData jacketData) {
        this.jacketData = jacketData;
    }
}
