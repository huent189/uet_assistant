package vnu.uet.mobilecourse.assistant.work.remindHandler;

import android.content.Context;
import android.util.Log;

import androidx.work.Data;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import vnu.uet.mobilecourse.assistant.model.firebase.CourseSession;
import vnu.uet.mobilecourse.assistant.util.SessionConverter;
import vnu.uet.mobilecourse.assistant.work.reminder.SessionReminder;

@Deprecated
public class CourseHandler extends RemindHandler<CourseSession> {

    private static CourseHandler instance;

    private static final String COURSE_REMIND_WORK_TAG = CourseHandler.class.getName();

    public static CourseHandler getInstance() {
        if (instance == null) {
            instance = new CourseHandler();
        }

        return instance;
    }

    @Override
    protected String getWorkId(CourseSession session) {
        return session.getId();
    }

    @Override
    public void cancel(Context context, CourseSession session) {
        WorkManager.getInstance(context)
                .cancelAllWorkByTag(session.getId());
    }

    @Override
    protected long calculateInitialDelayTime(CourseSession session) {
        Calendar dueDate = Calendar.getInstance();
        Date dueTime = SessionConverter.toTime(session, new Date());
        dueDate.setTime(dueTime);

        if (dueDate.before(Calendar.getInstance())) {
            dueDate.add(Calendar.DAY_OF_MONTH, 7);
        }

        Log.d(COURSE_REMIND_WORK_TAG, dueDate.toString());

        return dueDate.getTimeInMillis() - System.currentTimeMillis();
    }

    @Override
    protected WorkRequest buildRequest(CourseSession session) {
        long delayTime = calculateInitialDelayTime(session);

        Data inputData = buildInputData(session);

        return new PeriodicWorkRequest
                .Builder(SessionReminder.class, 7, TimeUnit.DAYS)
                .setInitialDelay(delayTime, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag(session.getId())
                .addTag(session.getCourseCode())
                .addTag(COURSE_REMIND_WORK_TAG)
                .build();
    }

    @Override
    protected Data buildInputData(CourseSession session) {
        return new Data.Builder()
                .putInt("type", session.getType())
                .putString("courseName", session.getCourseName())
                .putString("courseCode", session.getCourseCode())
                .putString("classroom", session.getClassroom())
                .putString("teacherName", session.getTeacherName())
                .putInt("dayOfWeek", session.getDayOfWeek())
                .putInt("start", session.getStart())
                .putInt("end", session.getEnd())
                .build();
    }
}
