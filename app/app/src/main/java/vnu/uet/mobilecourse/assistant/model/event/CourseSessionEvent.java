package vnu.uet.mobilecourse.assistant.model.event;

import java.util.Date;

public class CourseSessionEvent implements IEvent {

    private String title;
    private String category;
    private Date time;

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public Date getTime() {
        return time;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean isCompleted() {
        return time.before(new Date());
    }
}
