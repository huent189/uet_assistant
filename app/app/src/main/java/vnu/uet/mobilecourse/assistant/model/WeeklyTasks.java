package vnu.uet.mobilecourse.assistant.model;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.Date;
import java.util.List;

public class WeeklyTasks extends ExpandableGroup {
    private Date begin;

    private Date end;

    public WeeklyTasks(String title, List<CourseTask> items) {
        super(title, items);
    }

    @Override
    public List<CourseTask> getItems() {
        return super.getItems();
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
