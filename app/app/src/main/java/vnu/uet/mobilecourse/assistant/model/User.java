package vnu.uet.mobilecourse.assistant.model;

import vnu.uet.mobilecourse.assistant.SharedPreferencesManager;
import vnu.uet.mobilecourse.assistant.util.CONST;

public class User {
    private static User user;
    private String token;
    private String userId;
    private String email;
    private long lastSynchonizedTime;
    public static User getInstance(){
        if(user == null){
            user = new User();
            user.token = SharedPreferencesManager.getStringValue(SharedPreferencesManager.TOKEN);
            user.email = SharedPreferencesManager.getStringValue(SharedPreferencesManager.REGISTER_EMAIL);
            user.userId = SharedPreferencesManager.getStringValue(SharedPreferencesManager.USER_ID);
            user.lastSynchonizedTime = SharedPreferencesManager.getLongValue(SharedPreferencesManager.LAST_SYNCHONIZE_TIME);
        }
        return user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        SharedPreferencesManager.setString(SharedPreferencesManager.TOKEN, token);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        SharedPreferencesManager.setString(SharedPreferencesManager.USER_ID, userId);
    }

    public String getEmail() {
        return email;
    }

    public String getStudentId() {
        return email.replace(CONST.VNU_EMAIL_DOMAIN, CONST.EMPTY);
    }

    public void setEmail(String email) {
        this.email = email;
        SharedPreferencesManager.setString(SharedPreferencesManager.REGISTER_EMAIL, email);
    }

    public long getLastSynchonizedTime() {
        return lastSynchonizedTime;
    }

    public void setLastSynchonizedTime(long lastSynchonizedTime) {
        this.lastSynchonizedTime = lastSynchonizedTime;
        SharedPreferencesManager.setLong(SharedPreferencesManager.LAST_SYNCHONIZE_TIME, lastSynchonizedTime);
    }
}
