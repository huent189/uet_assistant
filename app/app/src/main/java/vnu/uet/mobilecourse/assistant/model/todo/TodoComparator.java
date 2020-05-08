package vnu.uet.mobilecourse.assistant.model.todo;

import java.util.Comparator;

import vnu.uet.mobilecourse.assistant.model.todo.Todo;

public class TodoComparator implements Comparator<Todo> {
    @Override
    public int compare(Todo o1, Todo o2) {
        return o1.getDeadline().compareTo(o2.getDeadline());
    }
}