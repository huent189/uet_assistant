package vnu.uet.mobilecourse.assistant.work;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.firestore.util.Util;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.firebase.NotificationType;
import vnu.uet.mobilecourse.assistant.model.firebase.Notification_UserSubCol;
import vnu.uet.mobilecourse.assistant.repository.firebase.NotificationRepository;
import vnu.uet.mobilecourse.assistant.util.NotificationHelper;
import vnu.uet.mobilecourse.assistant.view.MainActivity;

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
        mContext = getApplicationContext();

        Data data = getInputData();
        String id = data.getString("id");
        String title = data.getString("title");
        title = "Đến hạn " + title;
        String desc = data.getString("description");
        String category = data.getString("todoList");

        Notification_UserSubCol notificationDoc = new Notification_UserSubCol();
        notificationDoc.setId(Util.autoId());
        notificationDoc.setCategory(category);
        notificationDoc.setTitle(title);
        notificationDoc.setDescription(desc);
        notificationDoc.setNotifyTime(System.currentTimeMillis() / 1000);
        notificationDoc.setType(NotificationType.TODO);
        notificationDoc.setReference(id);

        NotificationRepository.getInstance().add(notificationDoc);

        Log.e("TODO", "doWork: " + title );

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        Notification notification = NotificationHelper.getsInstance()
                .build(mContext, CHANNEL_ID, R.drawable.ic_check_circle_24dp, title, desc);

        NotificationHelper.getsInstance().notify(mContext, id, notification);

        return Result.success();
        // (Returning RETRY tells WorkManager to try this task again
        // later; FAILURE says not to try again.)
    }
}