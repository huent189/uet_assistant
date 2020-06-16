package vnu.uet.mobilecourse.assistant.alarm.scheduler;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.Date;

import vnu.uet.mobilecourse.assistant.alarm.receiver.TodoReceiver;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseSession;
import vnu.uet.mobilecourse.assistant.util.ParcelableUtils;
import vnu.uet.mobilecourse.assistant.util.SessionConverter;

public class TestScheduler extends Scheduler<String> {

    public static final String ACTION = "REMIND_TEST";

    private static TestScheduler instance;

    public static TestScheduler getInstance(Context context) {
        if (instance  == null) {
            instance = new TestScheduler(context);
        }

        return instance;
    }

    private TestScheduler(Context context) {
        super(context);
    }

    @Override
    protected Intent buildIntent(String content) {
        Intent intent = new Intent(mContext, TodoReceiver.class);
        intent.setAction(ACTION);
        intent.putExtra("data", content);
        return intent;
    }

    @Override
    protected int getRequestCode(String content) {
        return content.hashCode();
    }

    @Override
    protected long getTimeInMillis(String content) {
        return System.currentTimeMillis() + 10000;
    }

    @Override
    public void schedule(String session) {
        scheduleInterval(session, AlarmManager.INTERVAL_FIFTEEN_MINUTES);
    }
}
