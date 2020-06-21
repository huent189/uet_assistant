package vnu.uet.mobilecourse.assistant.model.event;

import java.util.Collection;
import java.util.Iterator;
import java.util.PriorityQueue;

public abstract class AbstractEventList<T extends IEvent> extends PriorityQueue<T> {

    public AbstractEventList() {
        super(new EventComparator());
    }

    public T get(int index) {
        if (index < 0 || index >= size())
        throw new ArrayIndexOutOfBoundsException();

        Iterator<T> iterator = iterator();

        T result = null;

        for (int i = 0; i <= index; i++) {
            result = iterator.next();
        }

        return result;
    }
}
