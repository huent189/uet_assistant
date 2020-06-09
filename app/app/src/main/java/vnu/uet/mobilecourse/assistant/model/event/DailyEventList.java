package vnu.uet.mobilecourse.assistant.model.event;

import java.util.Date;

public class DailyEventList extends AbstractEventList<IEvent> {
    private Date date;

    public DailyEventList(Date date) {
        super();
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
