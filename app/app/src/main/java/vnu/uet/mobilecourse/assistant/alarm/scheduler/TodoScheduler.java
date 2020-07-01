package vnu.uet.mobilecourse.assistant.alarm.scheduler;

import android.content.Context;
import android.content.Intent;

import vnu.uet.mobilecourse.assistant.alarm.receiver.TodoReceiver;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.util.ParcelableUtils;

public class TodoScheduler extends Scheduler<Todo> {

    public static final String ACTION = "REMIND_TODO";

    private static TodoScheduler instance;

    public static TodoScheduler getInstance(Context context) {
        if (instance  == null) {
            instance = new TodoScheduler(context);
        }

        return instance;
    }

    private TodoScheduler(Context context) {
        super(context);
    }

    @Override
    protected Intent buildIntent(Todo todo) {
        Intent intent = new Intent(mContext, TodoReceiver.class);
        intent.setAction(ACTION);
        intent.putExtra("todo", ParcelableUtils.toBytes(todo));
        return intent;
    }

    @Override
    protected int getRequestCode(Todo todo) {
        return todo.getId().hashCode();
    }

    @Override
    protected long getTimeInMillis(Todo todo) {
        return todo.getDeadline() * 1000;
    }

    @Override
    public void schedule(Todo todo) {
        scheduleOneTime(todo);
    }
}
