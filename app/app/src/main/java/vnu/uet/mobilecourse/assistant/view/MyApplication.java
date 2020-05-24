package vnu.uet.mobilecourse.assistant.view;

import android.app.Application;

@Deprecated
public class MyApplication extends Application {
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
    public static MyApplication getInstance(){
        return instance;
    }
}
