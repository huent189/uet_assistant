package vnu.uet.mobilecourse.assistant.model.todo;

import java.util.Comparator;
import java.util.Date;

import vnu.uet.mobilecourse.assistant.model.FirebaseModel.TodoDocument;
import vnu.uet.mobilecourse.assistant.model.todo.Todo;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;

public class TodoComparator implements Comparator<TodoDocument> {
    @Override
    public int compare(TodoDocument o1, TodoDocument o2) {
        return Integer.compare(o1.getDeadline(), o2.getDeadline());
    }
}