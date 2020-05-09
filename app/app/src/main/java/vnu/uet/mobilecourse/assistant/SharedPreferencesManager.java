package vnu.uet.mobilecourse.assistant;

import android.content.Context;
import android.content.SharedPreferences;
import vnu.uet.mobilecourse.assistant.view.MyApplication;

public class SharedPreferencesManager {
    private static SharedPreferences preferences;
    public static final String TEMP_EMAIL_ID = "TEMP_EMAIl_ID";
    public static final String USER_ID = "USER_ID";
    public static final String TOKEN = "TOKEN";
    public static final String REGISTER_EMAIL = "REGISTER_EMAIL";
    private static final String APP_SHARED_PREFS = "MySharedPrefs";
    private static void initPreference(){
        if (preferences == null){
            preferences = MyApplication.getInstance().getSharedPreferences(SharedPreferencesManager.APP_SHARED_PREFS,
                    Context.MODE_PRIVATE);
        }
    }
    public static String getValue(String key){
        initPreference();
        return preferences.getString(key, null);
    }
    public static void deleteKey(String key){
        initPreference();
        preferences.edit().remove(key).apply();
    }
    public static void setString(String key, String value){
        initPreference();
        preferences.edit().putString(key, value).apply();
    }
    public static void clearAll(){
        initPreference();
        preferences.edit().clear().apply();
    }
}
