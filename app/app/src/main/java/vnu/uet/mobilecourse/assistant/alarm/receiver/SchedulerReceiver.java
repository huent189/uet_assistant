package vnu.uet.mobilecourse.assistant.alarm.receiver;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.notification.Notification_UserSubCol;
import vnu.uet.mobilecourse.assistant.repository.firebase.NavigationBadgeRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.NotificationRepository;
import vnu.uet.mobilecourse.assistant.util.NotificationHelper;

public abstract class SchedulerReceiver<T> extends BroadcastReceiver {

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
     * @param notificationDoc contains data of notification
     */
    protected void pushNotification(Context context, Notification_UserSubCol notificationDoc) {
        String title = notificationDoc.getTitle();
        String desc = notificationDoc.getDescription();
        String notificationId = notificationDoc.getId();

        Notification notification = NotificationHelper.getsInstance()
                .build(context, getClass().getName(), R.drawable.ic_check_circle_24dp, title, desc);

        NotificationHelper.getsInstance().notify(context, notificationId, notification);
    }

    protected abstract String getAction();

    protected abstract T build(Intent intent);

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String intentAction = intent.getAction();

            if (getAction().equals(intentAction)) {
                T model = build(intent);

                // add a notification onto firebase db
                Notification_UserSubCol notificationDoc = generateNotification(model);
                NotificationRepository.getInstance().add(notificationDoc);

                // increase new notifications counter
                NavigationBadgeRepository.getInstance().increaseNewNotifications();

                // push notification onto device
                pushNotification(context.getApplicationContext(), notificationDoc);
            }
        }

    }
}
