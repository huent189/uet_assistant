package vnu.uet.mobilecourse.assistant.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import vnu.uet.mobilecourse.assistant.view.MyCoursesActivity;
import vnu.uet.mobilecourse.assistant.view.notification.NotificationsFragment;

public class NotificationHelper {
    private static final String TAG = NotificationHelper.class.getSimpleName();

    private static NotificationHelper sInstance;

    public static NotificationHelper getsInstance() {
        if (sInstance == null) {
            sInstance = new NotificationHelper();
        }

        return sInstance;
    }

    public static final String ACTION_OPEN = "OPEN_NOTIFICATIONS";

    public Notification build(@NonNull Context context, @NonNull String channelId,
                              int iconResId, String title, String content) {

        return build(context, channelId, iconResId, title, content, NotificationsFragment.class.getName());
    }

    public Notification build(@NonNull Context context, @NonNull String channelId,
                              int iconResId, String title, String content, String destFragment) {

        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(context, MyCoursesActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        resultIntent.setAction(ACTION_OPEN);
        resultIntent.putExtra("fragment", destFragment);

        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(iconResId)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(resultPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        return builder.build();
    }

    public void notify(@NonNull Context context, String notificationId, Notification notification) {
        NotificationManager notifyManager = context.getSystemService(NotificationManager.class);

        try {
            String channelId = notification.getChannelId();
            Log.e(TAG, "notify: " + channelId);

            if (notifyManager != null) {
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                createChannel(notificationManager, channelId);
                notificationManager.notify(notificationId.hashCode(), notification);
            }

        } catch (Exception e) {
            Log.e(TAG, "notify: " + e.getMessage());
        }

    }

    private void createChannel(@NonNull NotificationManagerCompat notificationManager,
                               @NonNull String id) throws Exception {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(id, id, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(id);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);

        } else throw new Exception("App notification hasn't supported for current SDK version");
    }
}
