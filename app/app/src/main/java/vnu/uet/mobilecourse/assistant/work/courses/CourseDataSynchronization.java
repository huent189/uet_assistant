package vnu.uet.mobilecourse.assistant.work.courses;

import androidx.work.*;
import vnu.uet.mobilecourse.assistant.view.MyApplication;

import java.util.concurrent.TimeUnit;

public class CourseDataSynchronization {
    private static final String TAG = "CourseDataSynchronization";
    public static void start(){
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest syncDataWork = new PeriodicWorkRequest.Builder(CourseSyncDataWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();
        PeriodicWorkRequest syncFinalExam = new PeriodicWorkRequest.Builder(SyncFinalExamWorker.class, 1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build();
        WorkManager manager = WorkManager.getInstance(MyApplication.getInstance().getApplicationContext());
        manager.enqueueUniquePeriodicWork(TAG + "_courses", ExistingPeriodicWorkPolicy.REPLACE, syncDataWork);
        manager.enqueueUniquePeriodicWork(TAG + "_portal", ExistingPeriodicWorkPolicy.REPLACE, syncFinalExam);


    }

}
