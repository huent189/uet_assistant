package vnu.uet.mobilecourse.assistant.work.reminder;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import vnu.uet.mobilecourse.assistant.model.notification.Notification_UserSubCol;
import vnu.uet.mobilecourse.assistant.repository.firebase.NotificationRepository;

public abstract class RemindWorker<T> extends Worker {

    private Context mContext;

    protected RemindWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }

    /**
     * Generate firebase notification document
     *
     * @param model contains data of notification
     * @return notification doc
     */
    protected abstract Notification_UserSubCol generateNotification(T model);

    /**
     * Push notification onto device
     *
     * @param context app context
     * @param model contains data of notification
     */
    protected abstract void pushNotification(Context context, T model);

    protected abstract T build(Data data);

    @NonNull
    @Override
    public Result doWork() {
        // Method to trigger an instant notification
        mContext = getApplicationContext();

        Data inputData = getInputData();

        T model = build(inputData);

        // add a notification onto firebase db
        Notification_UserSubCol notificationDoc = generateNotification(model);
        NotificationRepository.getInstance().add(notificationDoc);

        // push notification onto device
        pushNotification(mContext, model);

        // (Returning RETRY tells WorkManager to try this task again
        // later; FAILURE says not to try again.)
        return Result.success();
    }
}
