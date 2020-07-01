package vnu.uet.mobilecourse.assistant.alarm.scheduler;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.Date;

import vnu.uet.mobilecourse.assistant.alarm.receiver.SessionReceiver;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseSession;
import vnu.uet.mobilecourse.assistant.util.ParcelableUtils;
import vnu.uet.mobilecourse.assistant.util.SessionConverter;

public class SessionScheduler extends Scheduler<CourseSession> {

    public static final String ACTION = "REMIND_COURSE_SESSION";

    private static SessionScheduler instance;

    public static SessionScheduler getInstance(Context context) {
        if (instance  == null) {
            instance = new SessionScheduler(context);
        }

        return instance;
    }

    private SessionScheduler(Context context) {
        super(context);
    }

    @Override
    protected Intent buildIntent(CourseSession session) {
        Intent intent = new Intent(mContext, SessionReceiver.class);
        intent.setAction(ACTION);
        intent.putExtra("session", ParcelableUtils.toBytes(session));
        return intent;
    }

    @Override
    protected int getRequestCode(CourseSession session) {
        return session.getId().hashCode();
    }

    @Override
    protected long getTimeInMillis(CourseSession session) {
        Calendar dueDate = Calendar.getInstance();
        Date dueTime = SessionConverter.toTime(session, new Date());
        dueDate.setTime(dueTime);

        if (dueDate.before(Calendar.getInstance())) {
            dueDate.add(Calendar.DAY_OF_MONTH, INTERVAL_DAY_STEP);
        }

        return dueDate.getTimeInMillis();
    }

    @Override
    public void schedule(CourseSession session) {
        scheduleInterval(session, AlarmManager.INTERVAL_DAY * INTERVAL_DAY_STEP);
    }

    private static final int INTERVAL_DAY_STEP = 7;
}
