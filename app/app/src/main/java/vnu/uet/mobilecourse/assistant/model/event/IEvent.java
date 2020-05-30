package vnu.uet.mobilecourse.assistant.model.event;

import java.util.Date;

public interface IEvent {
    String getTitle();
    String getCategory();
    Date getTime();
    boolean isCompleted();
}
