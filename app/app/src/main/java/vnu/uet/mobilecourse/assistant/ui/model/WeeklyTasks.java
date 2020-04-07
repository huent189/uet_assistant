package vnu.uet.mobilecourse.assistant.ui.model;

import java.util.ArrayList;
import java.util.Date;

public class WeeklyTasks extends ArrayList<CourseTask> {
    private Date begin;

    private Date end;

    public WeeklyTasks(Date begin, Date end) {
        this.begin = begin;
        this.end = end;
    }

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
