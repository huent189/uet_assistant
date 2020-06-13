package vnu.uet.mobilecourse.assistant.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {
    private static final String TAG = NotificationHelper.class.getSimpleName();

    private static NotificationHelper sInstance;

    public static NotificationHelper getsInstance() {
        if (sInstance == null) {
            sInstance = new NotificationHelper();
        }

        return sInstance;
    }

    public Notification build(@NonNull Context context, @NonNull String channelId,
                              int iconResId, String title, String content) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(iconResId)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        return builder.build();
    }

    public boolean notify(@NonNull Context context, String notificationId, Notification notification) {
        NotificationManager notifyManager = context.getSystemService(NotificationManager.class);

        try {
            String channelId = notification.getChannelId();
            Log.e(TAG, "notify: " + channelId);

            if (notifyManager != null) {
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                createChannel(notificationManager, channelId);
                notificationManager.notify(notificationId.hashCode(), notification);
                return true;
            }

        } catch (Exception e) {
            Log.e(TAG, "notify: " + e.getMessage());
        }

        return false;
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
