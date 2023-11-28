package com.feup.pesi.calmdown.model;

public class Quizz {
    private int userStressLevel;
    private String userRelaxPreference;
    private String userColor;
    private boolean userNotified;

    // Construtor
    public Quizz(int userStressLevel, String userRelaxPreference, String userColor, boolean userNotified) {
        this.userStressLevel = userStressLevel;
        this.userRelaxPreference = userRelaxPreference;
        this.userColor = userColor;
        this.userNotified = userNotified;
    }

    // Editar
    public int getUserStressLevel() {
        return userStressLevel;
    }

    public void setUserStressLevel(int userStressLevel) {
        this.userStressLevel = userStressLevel;
    }

    public String getUserRelaxPreference() {
        return userRelaxPreference;
    }

    public void setUserRelaxPreference(String userRelaxPreference) {
        this.userRelaxPreference = userRelaxPreference;
    }

    public String getUserColor() {
        return userColor;
    }

    public void setUserColor(String userColor) {
        this.userColor = userColor;
    }

    public boolean isUserNotified() {
        return userNotified;
    }

    public void setUserNotified(boolean userNotified) {
        this.userNotified = userNotified;
    }

    // Exibir informações do quizz
    public void displayQuizzInfo() {
        System.out.println("User Stress Level: " + userStressLevel);
        System.out.println("User Relax Preference: " + userRelaxPreference);
        System.out.println("User Color: " + userColor);
        System.out.println("User Notified: " + userNotified);
    }
}
