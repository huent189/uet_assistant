package vnu.uet.mobilecourse.assistant.work.courses;

import androidx.work.*;
import vnu.uet.mobilecourse.assistant.view.MyApplication;

import java.util.concurrent.TimeUnit;

public class CourseDataSynchronization {
    static final String TAG = "CourseDataSynchronization";
    public static void start(){
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest syncDataWork = new PeriodicWorkRequest.Builder(CourseSyncDataWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(MyApplication.getInstance().getApplicationContext())
                .enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.REPLACE, syncDataWork);
    }

}
