package vnu.uet.mobilecourse.assistant.model;

import android.content.Context;
import android.content.SharedPreferences;
import vnu.uet.mobilecourse.assistant.view.MyApplication;

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
        if(token == null){
            preferences = MyApplication.getInstance().getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
            token = preferences.getString("TOKEN", null);
        }

        return token;
    }

    public static void setToken(String token) {
        Token.token = token;
        //TODO: intergation test
        if (preferences == null){
            preferences = MyApplication.getInstance().getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
            preferences.edit().putString("TOKEN", token).apply();
        }
    }
    // for testing only
    public static void clearToken(){
        token = null;
    }
}
