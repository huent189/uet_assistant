package vnu.uet.mobilecourse.assistant.model.todo;

import java.util.Date;

public class DailyTodoList extends AbstractTodoList {
    private Date date;

    public DailyTodoList(Date date) {
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
