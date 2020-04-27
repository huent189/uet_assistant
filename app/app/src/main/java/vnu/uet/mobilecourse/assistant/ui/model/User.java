package vnu.uet.mobilecourse.assistant.ui.model;

import android.content.SharedPreferences;

public class User {
    public User() {
    }
    private SharedPreferences preferences;
    private String studentNumber;
    private String password;
    private String privateToken;
    private String token;
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPrivateToken() {
        if(privateToken == null){
            preferences.getString("TOKEN", "");
        }
        return privateToken;
    }

    public void setPrivateToken(String privateToken) {
        this.privateToken = privateToken;
        preferences.edit().putString("TOKEN", privateToken);
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
