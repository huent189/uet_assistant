package vnu.uet.mobilecourse.assistant.model;

import java.util.Comparator;

public class TodoComparator implements Comparator<Todo> {
    @Override
    public int compare(Todo o1, Todo o2) {
        return o1.getDeadline().compareTo(o2.getDeadline());
    }
}