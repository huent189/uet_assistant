package vnu.uet.mobilecourse.assistant.work.remindHandler;

import android.content.Context;

import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

@Deprecated
public abstract class RemindHandler<T> {

    public void schedule(Context context, T item) {
        WorkManager workManager = WorkManager.getInstance(context);

        // build request
        WorkRequest request = buildRequest(item);

        // schedule work request
        String workId = getWorkId(item);

        // one time work request
        if (request instanceof OneTimeWorkRequest) {
            OneTimeWorkRequest oneTimeRequest = (OneTimeWorkRequest) request;
            workManager.enqueueUniqueWork(workId, ExistingWorkPolicy.KEEP, oneTimeRequest);

        }
        // periodic work request
        // TODO: change course session time/classroom?
        else if (request instanceof PeriodicWorkRequest) {
            PeriodicWorkRequest periodicRequest = (PeriodicWorkRequest) request;
            workManager.enqueueUniquePeriodicWork(workId, ExistingPeriodicWorkPolicy.KEEP, periodicRequest);

        }
    }

    protected abstract String getWorkId(T item);

    public abstract void cancel(Context context, T item);

    public void cancel(Context context, String workId) {
        WorkManager.getInstance(context).cancelUniqueWork(workId);
    }

    /**
     * @return mili second
     */
    protected abstract long calculateInitialDelayTime(T item);

    protected abstract WorkRequest buildRequest(T item);

    protected abstract Data buildInputData(T item);
}
