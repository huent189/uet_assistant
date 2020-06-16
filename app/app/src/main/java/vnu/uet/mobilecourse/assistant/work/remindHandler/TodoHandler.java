package vnu.uet.mobilecourse.assistant.work.remindHandler;

import android.content.Context;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.concurrent.TimeUnit;

import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.work.reminder.TodoReminder;

@Deprecated
public class TodoHandler extends RemindHandler<Todo> {

    private static TodoHandler instance;

    private static final String TODO_REMIND_WORK_TAG = TodoHandler.class.getName();

    public static TodoHandler getInstance() {
        if (instance == null) {
            instance = new TodoHandler();
        }

        return instance;
    }

    public void cancelAll(Context context) {
        WorkManager.getInstance(context)
                .cancelAllWorkByTag(TODO_REMIND_WORK_TAG);
    }

    @Override
    protected String getWorkId(Todo todo) {
        return todo.getId();
    }

    @Override
    public void cancel(Context context, Todo todo) {
        WorkManager.getInstance(context)
                .cancelAllWorkByTag(todo.getId());
    }

    public void cancelByTodoList(Context context, String todoListId) {
        WorkManager.getInstance(context)
                .cancelAllWorkByTag(todoListId);
    }

    @Override
    protected long calculateInitialDelayTime(Todo item) {
        return item.getDeadline() * 1000 - System.currentTimeMillis();
    }

    @Override
    protected WorkRequest buildRequest(Todo todo) {
        long delayTime = calculateInitialDelayTime(todo);

        Data inputData = buildInputData(todo);

        return new OneTimeWorkRequest.Builder(TodoReminder.class)
                .setInitialDelay(delayTime, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag(todo.getId())
                .addTag(todo.getTodoListId())
                .addTag(TODO_REMIND_WORK_TAG)
                .build();
    }

    @Override
    protected Data buildInputData(Todo todo) {
        return new Data.Builder()
                .putString("id", todo.getId())
                .putString("title", todo.getTitle())
                .putString("description", todo.getDescription())
                .putString("ownerId", todo.getOwnerId())
                .putString("todoListId", todo.getTodoListId())
                .putLong("deadline", todo.getDeadline())
                .putBoolean("completed", todo.isCompleted())
                .putString("category", todo.getCategory())
                .build();
    }
}
