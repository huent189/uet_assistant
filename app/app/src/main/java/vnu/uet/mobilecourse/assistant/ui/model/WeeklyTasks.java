package vnu.uet.mobilecourse.assistant.ui.model;

import android.os.Parcel;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WeeklyTasks extends ExpandableGroup {
    private Date begin;

    private Date end;

    public WeeklyTasks(String title, List items) {
        super(title, items);
    }

//    protected WeeklyTasks(Parcel in) {
//        super(in);
//    }

//    public WeeklyTasks(Date begin, Date end) {
//        this.begin = begin;
//        this.end = end;
//    }

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
