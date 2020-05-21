package vnu.uet.mobilecourse.assistant.util;

import android.app.Notification;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import vnu.uet.mobilecourse.assistant.R;

public class NotificationHelper {
    public static NotificationHelper sInstance;

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
}
