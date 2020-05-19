package vnu.uet.mobilecourse.assistant.model.todo;

import java.util.Comparator;

import vnu.uet.mobilecourse.assistant.model.FirebaseModel.Todo;

public class TodoComparator implements Comparator<Todo> {
    @Override
    public int compare(Todo o1, Todo o2) {
        return Long.compare(o1.getDeadline(), o2.getDeadline());
    }
}