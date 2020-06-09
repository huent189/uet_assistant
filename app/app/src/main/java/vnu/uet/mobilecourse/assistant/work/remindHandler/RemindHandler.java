package vnu.uet.mobilecourse.assistant.work.remindHandler;

import android.content.Context;

import androidx.work.Data;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

public abstract class RemindHandler<T> {

    public void schedule(Context context, T item) {
        WorkRequest request = buildRequest(item);

        // schedule work request
        WorkManager.getInstance(context).enqueue(request);
    }

    public abstract void cancel(Context context, T item);

    /**
     * @return mili second
     */
    protected abstract long calculateInitialDelayTime(T item);

    protected abstract WorkRequest buildRequest(T item);

    protected abstract Data buildInputData(T item);
}
