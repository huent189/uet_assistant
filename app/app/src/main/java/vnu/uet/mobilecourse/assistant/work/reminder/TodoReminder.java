package vnu.uet.mobilecourse.assistant.work.reminder;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.WorkerParameters;

import com.google.firebase.firestore.util.Util;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.model.notification.Notification_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.notification.TodoNotification;
import vnu.uet.mobilecourse.assistant.util.NotificationHelper;

@Deprecated
public class TodoReminder extends RemindWorker<Todo> {

    private static final String CHANNEL_ID = TodoReminder.class.getName();

    public TodoReminder(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

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
    protected void pushNotification(Context context, Notification_UserSubCol notificationDoc) {
        String title = notificationDoc.getTitle();
        String desc = notificationDoc.getDescription();
        String notificationId = notificationDoc.getId();

        Notification notification = NotificationHelper.getsInstance()
                .build(context, CHANNEL_ID, R.drawable.ic_check_circle_24dp, title, desc);

        NotificationHelper.getsInstance().notify(context, notificationId, notification);
    }

    @Override
    protected Todo build(Data data) {
        Todo todo = new Todo();

        String id = data.getString("id");
        todo.setId(id);

        String title = data.getString("title");
        todo.setTitle(title);

        String desc = data.getString("description");
        todo.setDescription(desc);

        String ownerId = data.getString("ownerId");
        todo.setOwnerId(ownerId);

        String todoListId = data.getString("todoListId");
        todo.setTodoListId(todoListId);

        long deadline = data.getLong("deadline", 0);
        todo.setDeadline(deadline);

        boolean completed = data.getBoolean("completed", false);
        todo.setCompleted(completed);

        String category = data.getString("category");
        todo.setCategory(category);

        return todo;
    }
}