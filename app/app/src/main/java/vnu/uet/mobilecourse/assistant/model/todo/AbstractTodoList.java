package vnu.uet.mobilecourse.assistant.model.todo;

import java.util.Iterator;
import java.util.PriorityQueue;

public class AbstractTodoList extends PriorityQueue<Todo> {

    public AbstractTodoList() {
        super(new TodoComparator());
    }

    public Todo get(int index) {
        if (index < 0 || index >= size())
        throw new ArrayIndexOutOfBoundsException();

        Iterator<Todo> iterator = iterator();

        Todo result = null;

        for (int i = 0; i <= index; i++) {
        result = iterator.next();
        }

        return result;
    }
}