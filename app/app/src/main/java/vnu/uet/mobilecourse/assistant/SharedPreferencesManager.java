package vnu.uet.mobilecourse.assistant;

import android.content.Context;
import android.content.SharedPreferences;
import vnu.uet.mobilecourse.assistant.view.MyApplication;

public class SharedPreferencesManager {

    private static SharedPreferences preferences;
    public static final String USER_ID = "USER_ID";
    public static final String TOKEN = "TOKEN";
    public static final String REGISTER_EMAIL = "REGISTER_EMAIL";
    public static final String LAST_SYNCHONIZE_TIME = "LAST_SYNCHONIZE_TIME";
    public static final String NEW_NOTIFICATION = "NEW_NOTIFICATION";
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

    public static long getLongValue(String key){
        initPreference();
        return preferences.getLong(key, -1);
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

    public static void setInt(String key, int value){
        initPreference();
        preferences.edit().putInt(key, value).apply();
    }

    public static int getInt(String key){
        initPreference();
        return preferences.getInt(key, 0);
    }

    public static void clearAll(){
        initPreference();
        preferences.edit().clear().apply();
    }

    public static void registerOnChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        initPreference();
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }
}
