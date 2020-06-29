package vnu.uet.mobilecourse.assistant.work.courses;

import androidx.work.*;
import vnu.uet.mobilecourse.assistant.view.MyApplication;

public class CourseActionSynchronization {
    public static void scheduleUpdateMaterialCompletion(int materialId){
        String TAG = "update_completion_" + materialId;
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        Data.Builder data = new Data.Builder();
        data.putInt(CourseActionWorker.MATERIAL_ID, materialId);

        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(CourseActionWorker.class)
                .addTag(TAG).setInputData(data.build()).setConstraints(constraints).build();
        WorkManager.getInstance(MyApplication.getInstance()).enqueueUniqueWork(TAG, ExistingWorkPolicy.REPLACE,work);

    }
}
