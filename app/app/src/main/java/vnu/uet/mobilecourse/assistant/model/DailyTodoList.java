package vnu.uet.mobilecourse.assistant.model;

import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.PriorityQueue;

public class DailyTodoList extends PriorityQueue<Todo> {
    private Date date;

    public DailyTodoList(Date date) {
        super(new TodoComparator());
        this.date = date;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
