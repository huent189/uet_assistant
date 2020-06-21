package vnu.uet.mobilecourse.assistant.model.forum;

import java.util.Comparator;

public class DiscussionComparator implements Comparator<Discussion> {

    @Override
    public int compare(Discussion o1, Discussion o2) {
        if ((o1.isPinned() && o2.isPinned()) || (!o1.isPinned() && !o2.isPinned())) {
            return Long.compare(o1.getTimeCreated(), o2.getTimeCreated()) * -1;
        } else if (o1.isPinned()) {
            return -1;
        } else {
            return 1;
        }
    }
}
