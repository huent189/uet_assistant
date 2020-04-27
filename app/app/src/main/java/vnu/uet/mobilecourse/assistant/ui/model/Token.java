package vnu.uet.mobilecourse.assistant.ui.model;

import android.content.Context;
import android.content.SharedPreferences;

public class Token {
    private static final String preferenceName = "UET_ASSITANT";
    private static SharedPreferences preferences;
    private static String token;
    public static void init(Context context){
        if(preferences == null){
            preferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        }
    }

    public static String getToken() {
        //TODO: intergation test
//        if(token == null){
//            token = preferences.getString("TOKEN", null);
//        }
        return token;
    }

    public static void setToken(String token) {
        Token.token = token;
        //TODO: intergation test
//        preferences.edit().putString("TOKEN", token).apply();
    }
    // for testing only
    public static void clearToken(){
        token = null;
    }
}
