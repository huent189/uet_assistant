package vnu.uet.mobilecourse.assistant.alarm.receiver;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.firestore.util.Util;

import vnu.uet.mobilecourse.assistant.alarm.scheduler.TestScheduler;
import vnu.uet.mobilecourse.assistant.alarm.scheduler.TodoScheduler;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.model.notification.Notification_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.notification.TodoNotification;
import vnu.uet.mobilecourse.assistant.util.ParcelableUtils;

public class TestReceiver extends SchedulerReceiver<String> {

    @SuppressLint("RestrictedApi")
    @Override
    protected Notification_UserSubCol generateNotification(String content) {
        String id = content;
        String title = content;
        String desc = content;

        TodoNotification notificationDoc = new TodoNotification();
        notificationDoc.setId(Util.autoId());
        notificationDoc.setTitle(title);
        notificationDoc.setDescription(desc);
        notificationDoc.setNotifyTime(System.currentTimeMillis() / 1000);
        notificationDoc.setTodoId(id);

        return notificationDoc;
    }

    @Override
    protected String getAction() {
        return TestScheduler.ACTION;
    }

    @Override
    protected String build(Intent intent) {
        String content = null;

        Bundle extras = intent.getExtras();
        if (extras != null) {
            content = extras.getString("data");
        }

        return content;
    }
}
