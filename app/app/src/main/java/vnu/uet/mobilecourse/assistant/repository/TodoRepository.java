package vnu.uet.mobilecourse.assistant.repository;

import java.util.HashMap;
import java.util.Map;

import vnu.uet.mobilecourse.assistant.model.DailyTodoList;

public class TodoRepository {
    private static TodoRepository instance;

    private Map<String, DailyTodoList> todoListMap = new HashMap<>();


    public static TodoRepository getInstance() {
        if (instance == null)
            instance = new TodoRepository();

        return instance;
    }
}
