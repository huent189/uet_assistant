package vnu.uet.mobilecourse.assistant.model;

import android.app.AlarmManager;
import vnu.uet.mobilecourse.assistant.SharedPreferencesManager;
import vnu.uet.mobilecourse.assistant.util.StringConst;

public class User {

    private static User user;
    private String token;
    private String userId;
    private String email;
    private String name;
    private long dob;
    private String uetClass;
    private boolean enableSyncNoti;
    private long lastSyncTime;

    public static User getInstance(){
        if(user == null){
            user = new User();
        }

        return user;
    }

    private User() {
        token = SharedPreferencesManager.getStringValue(TOKEN);
        email = SharedPreferencesManager.getStringValue(REGISTER_EMAIL);
        userId = SharedPreferencesManager.getStringValue(USER_ID);
        name = SharedPreferencesManager.getStringValue(NAME);
        dob = SharedPreferencesManager.getLongValue(DOB, 0);
        uetClass = SharedPreferencesManager.getStringValue(CLASS);
        enableSyncNoti = SharedPreferencesManager.getBooleanValue(ENABLE_SYNC_NOTI);
        lastSyncTime = SharedPreferencesManager.getLongValue(LAST_SYNC_TIME,
                System.currentTimeMillis() - AlarmManager.INTERVAL_DAY);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        SharedPreferencesManager.setString(TOKEN, token);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        SharedPreferencesManager.setString(USER_ID, userId);
    }

    public String getEmail() {
        return email;
    }

    public String getStudentId() {
        return email.replace(StringConst.VNU_EMAIL_DOMAIN, StringConst.EMPTY);
    }

    public void setEmail(String email) {
        this.email = email;
        SharedPreferencesManager.setString(REGISTER_EMAIL, email);
    }


    public boolean getEnableSyncNoti() {
        return enableSyncNoti;
    }

    public void setEnableSyncNoti(boolean enableSyncNoti) {
        this.enableSyncNoti = enableSyncNoti;
        SharedPreferencesManager.setBoolean(ENABLE_SYNC_NOTI, enableSyncNoti);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        SharedPreferencesManager.setString(NAME, name);
    }

    public long getDob() {
        return dob;
    }

    public void setDob(long dob) {
        this.dob = dob;
        SharedPreferencesManager.setLong(DOB, dob);
    }

    public String getUetClass() {
        return uetClass;
    }

    public void setUetClass(String uetClass) {
        this.uetClass = uetClass;
        SharedPreferencesManager.setString(CLASS, uetClass);
    }

    public long getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(long lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
        SharedPreferencesManager.setLong(LAST_SYNC_TIME, lastSyncTime);
    }

    private static final String USER_ID = "USER_ID";
    private static final String TOKEN = "TOKEN";
    private static final String REGISTER_EMAIL = "REGISTER_EMAIL";
    private static final String NAME = "FULL_NAME";
    private static final String DOB = "DAY_OF_BIRTH";
    private static final String CLASS = "UET_CLASS";
    private static final String ENABLE_SYNC_NOTI = "ENABLE_SYNC_NOTI";
    private static final String LAST_SYNC_TIME = "LAST_SYNC_TIME";

    public void reset(){
        user = new User();
    }
}
