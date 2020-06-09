package vnu.uet.mobilecourse.assistant.model.event;

import java.util.Comparator;

public class EventComparator implements Comparator<IEvent> {
    @Override
    public int compare(IEvent o1, IEvent o2) {
        return o1.getTime().compareTo(o2.getTime());
    }
}