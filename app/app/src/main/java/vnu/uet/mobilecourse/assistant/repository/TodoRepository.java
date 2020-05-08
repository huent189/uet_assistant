package vnu.uet.mobilecourse.assistant.repository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import vnu.uet.mobilecourse.assistant.model.todo.DailyTodoList;

public class TodoRepository {
    private static TodoRepository instance;

    private Map<String, DailyTodoList> dailyList = new HashMap<>();

    public static TodoRepository getInstance() {
        if (instance == null)
            instance = new TodoRepository();

        return instance;
    }

    public Map<String, DailyTodoList> getDailyLists() {
        return dailyList;
    }
}
