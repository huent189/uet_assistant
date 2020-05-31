package vnu.uet.mobilecourse.assistant.work;

import android.content.Context;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import vnu.uet.mobilecourse.assistant.model.firebase.Todo;

public class RemindScheduler {

    private static RemindScheduler instance;

    public static RemindScheduler getInstance() {
        if (instance == null) {
            instance = new RemindScheduler();
        }

        return instance;
    }

    public void enqueue(Context context, Todo todo) {
        long delayTime = todo.getDeadline() * 1000 - System.currentTimeMillis();

        Data inputData = new Data.Builder()
                .putString("id", todo.getId())
                .putString("title", todo.getTitle())
                .putString("description", todo.getDescription())
                .putString("todoList", todo.getTodoListId())
                .build();

        OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(TodoReminder.class)
                .setInitialDelay(delayTime, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag(todo.getId())
                .build();

        WorkManager.getInstance(context).enqueue(notificationWork);
    }
}
