package vnu.uet.mobilecourse.assistant.model.todo;

import java.util.Iterator;
import java.util.PriorityQueue;

import vnu.uet.mobilecourse.assistant.model.FirebaseModel.TodoDocument;

public abstract class AbstractTodoList extends PriorityQueue<TodoDocument> {

    public AbstractTodoList() {
        super(new TodoComparator());
    }

    public TodoDocument get(int index) {
        if (index < 0 || index >= size())
        throw new ArrayIndexOutOfBoundsException();

        Iterator<TodoDocument> iterator = iterator();

        TodoDocument result = null;

        for (int i = 0; i <= index; i++) {
            result = iterator.next();
        }

        return result;
    }
}
