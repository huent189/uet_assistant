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
        if (sInstance == null)
            sInstance = new NotificationHelper();

        return sInstance;
    }

    public Notification build(@NonNull Context context, @NonNull String channelId,
                              int icon, String title, String content) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        return builder.build();
    }

    public boolean notify(@NonNull Context context, String id, Notification notification) {
        NotificationManager notifyManager = context.getSystemService(NotificationManager.class);

        try {
            String channelId = notification.getChannelId();
            Log.e(TAG, "notify: " + channelId);
            boolean isChannelExist = isChannelExist(context, channelId);

//            if (!isChannelExist) {
                createChannel(context, channelId);
//            }

            if (notifyManager != null) {
                NotificationManagerCompat.from(context).notify(id.hashCode(), notification);
                return true;
            }

        } catch (Exception e) {
            Log.e(TAG, "notify: " + e.getMessage());
        }

        return false;
    }

    public boolean isChannelExist(@NonNull Context context, @NonNull String channelId) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

            if (notificationManager == null) {
                throw new Exception("Notification Manager not found");
            }

            return notificationManager.getNotificationChannel(channelId) == null;
        }

        throw new Exception("App notification hasn't supported for current SDK version");
    }

    public void createChannel(@NonNull Context context, @NonNull String id) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(id, id, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(id);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

            if (notificationManager == null) {
                throw new Exception("Notification Manager not found");
            }

            notificationManager.createNotificationChannel(channel);

        } else throw new Exception("App notification hasn't supported for current SDK version");
    }
}
