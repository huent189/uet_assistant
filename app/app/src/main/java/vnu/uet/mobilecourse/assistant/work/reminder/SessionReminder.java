package vnu.uet.mobilecourse.assistant.work.reminder;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.WorkerParameters;

import com.google.firebase.firestore.util.Util;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.notification.CourseAttendantNotification;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseSession;
import vnu.uet.mobilecourse.assistant.model.notification.Notification_UserSubCol;
import vnu.uet.mobilecourse.assistant.util.NotificationHelper;

@Deprecated
public class SessionReminder extends RemindWorker<CourseSession> {

    private static final String CHANNEL_ID = SessionReminder.class.getName();

    public SessionReminder(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected Notification_UserSubCol generateNotification(CourseSession session) {
        String title = "Đi học " + session.getCourseName();
        String desc = "Tại phòng " + session.getClassroom();
        String courseCode = session.getCourseCode();

        CourseAttendantNotification notificationDoc = new CourseAttendantNotification();
        notificationDoc.setId(Util.autoId());
        notificationDoc.setTitle(title);
        notificationDoc.setDescription(desc);
        notificationDoc.setNotifyTime(System.currentTimeMillis() / 1000);
        notificationDoc.setCourseCode(courseCode);

        return notificationDoc;
    }

    @Override
    protected void pushNotification(Context context, Notification_UserSubCol notificationDoc) {
        String title = notificationDoc.getTitle();
        String desc = notificationDoc.getDescription();
        String notificationId = notificationDoc.getId();

        Notification notification = NotificationHelper.getsInstance()
                .build(context, CHANNEL_ID, R.drawable.ic_check_circle_24dp, title, desc);

        NotificationHelper.getsInstance().notify(context, notificationId, notification);
    }

    @Override
    protected CourseSession build(Data data) {
        CourseSession session = new CourseSession();

        int type = data.getInt("type", 0);
        session.setType(type);

        String courseName = data.getString("courseName");
        session.setCourseName(courseName);

        String courseCode = data.getString("courseCode");
        session.setCourseCode(courseCode);

        String classroom = data.getString("classroom");
        session.setClassroom(classroom);

        String teacherName = data.getString("teacherName");
        session.setTeacherName(teacherName);

        int dayOfWeek = data.getInt("dayOfWeek", 0);
        session.setDayOfWeek(dayOfWeek);

        int start = data.getInt("start", 0);
        session.setStart(start);

        int end = data.getInt("end", 0);
        session.setEnd(end);

        return session;
    }
}