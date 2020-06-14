package vnu.uet.mobilecourse.assistant.alarm.receiver;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.firestore.util.Util;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.alarm.scheduler.TodoScheduler;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.model.notification.Notification_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.notification.TodoNotification;
import vnu.uet.mobilecourse.assistant.util.NotificationHelper;
import vnu.uet.mobilecourse.assistant.util.ParcelableUtils;

public class TodoReceiver extends SchedulerReceiver<Todo> {

    @SuppressLint("RestrictedApi")
    @Override
    protected Notification_UserSubCol generateNotification(Todo todo) {
        String id = todo.getId();
        String title = todo.getTitle();
        title = "Đến hạn: " + title;
        String desc = todo.getDescription();

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
        return TodoScheduler.ACTION;
    }

    @Override
    protected Todo build(Intent intent) {
        Todo todo = null;

        Bundle extras = intent.getExtras();
        if (extras != null) {
            byte[] bytes = (byte[]) extras.get("todo");
            todo = ParcelableUtils.toParcelable(bytes, Todo.CREATOR);
        }

        return todo;
    }
}
