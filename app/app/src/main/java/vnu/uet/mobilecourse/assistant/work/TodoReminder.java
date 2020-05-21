package vnu.uet.mobilecourse.assistant.work;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.util.NotificationHelper;

public class TodoReminder extends Worker {
    private static final String CHANNEL_ID = TodoReminder.class.getName();

    private Context mContext;

    public TodoReminder(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);

        mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        // Method to trigger an instant notification
        Data data = getInputData();
        String id = data.getString("id");
        String title = data.getString("title");
        String desc = data.getString("description");

        Notification notification = NotificationHelper.getsInstance()
                .build(mContext, CHANNEL_ID, R.drawable.ic_check_circle_24dp, title, desc);

        NotificationHelper.getsInstance().notify(mContext, id, notification);

        return Result.success();
        // (Returning RETRY tells WorkManager to try this task again
        // later; FAILURE says not to try again.)
    }
}