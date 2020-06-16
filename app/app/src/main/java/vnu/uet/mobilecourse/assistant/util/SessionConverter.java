package vnu.uet.mobilecourse.assistant.util;

import java.util.Calendar;
import java.util.Date;

import vnu.uet.mobilecourse.assistant.model.event.CourseSessionEvent;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseSession;

public class SessionConverter {
    public static Date toTime(CourseSession session, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, session.getDayOfWeek());
        calendar.set(Calendar.HOUR_OF_DAY, OPEN_HOUR + session.getStart() - 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTime();
    }

    private static int OPEN_HOUR = 7;

    private static String TITLE_PREFIX = "Đi học ";

    public static CourseSessionEvent toEvent(CourseSession session, Date date) {
        CourseSessionEvent event = new CourseSessionEvent();

        event.setTitle(TITLE_PREFIX + session.getCourseName());
        event.setCategory(session.getClassroom());
        event.setTime(toTime(session, date));

        return event;
    }
}
