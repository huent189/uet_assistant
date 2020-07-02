package vnu.uet.mobilecourse.assistant;

import android.content.Context;
import android.content.SharedPreferences;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.view.MyApplication;

public class SharedPreferencesManager {

    private static SharedPreferences preferences;
    private static final String APP_SHARED_PREFS = "MySharedPrefs";

    private static void initPreference(){
        if (preferences == null){
            preferences = MyApplication.getInstance()
                    .getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
        }
    }

    public static String getStringValue(String key){
        initPreference();
        return preferences.getString(key, null);
    }

    public static long getLongValue(String key, long defValue){
        initPreference();
        return preferences.getLong(key, defValue);
    }

    public static void deleteKey(String key){
        initPreference();
        preferences.edit().remove(key).apply();
    }

    public static void setString(String key, String value){
        initPreference();
        preferences.edit().putString(key, value).apply();
    }

    public static void setLong(String key, Long value){
        initPreference();
        preferences.edit().putLong(key, value).apply();
    }
    public static void setBoolean(String key, boolean value){
        initPreference();
        preferences.edit().putBoolean(key, value).apply();
    }

    public static boolean getBooleanValue(String key){
        initPreference();
        return preferences.getBoolean(key, false);
    }

    public static void clearAll(){
        initPreference();
        preferences.edit().clear().commit();
        User.getInstance().reset();
    }
}
