package vnu.uet.mobilecourse.assistant.alarm.receiver;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.firestore.util.Util;

import java.util.Locale;

import vnu.uet.mobilecourse.assistant.alarm.scheduler.SessionScheduler;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseSession;
import vnu.uet.mobilecourse.assistant.model.notification.CourseAttendantNotification;
import vnu.uet.mobilecourse.assistant.model.notification.Notification_UserSubCol;
import vnu.uet.mobilecourse.assistant.util.ParcelableUtils;

public class SessionReceiver extends SchedulerReceiver<CourseSession> {

    @SuppressLint("RestrictedApi")
    @Override
    protected Notification_UserSubCol generateNotification(CourseSession session) {
        String title = "Đi học " + session.getCourseName();
        String desc = String.format(
                Locale.ROOT,
                "Buổi học diễn ra từ tiết %d - %d tại %s",
                session.getStart(),
                session.getEnd(),
                session.getClassroom()
        );
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
    protected String getAction() {
        return SessionScheduler.ACTION;
    }

    @Override
    protected CourseSession build(Intent intent) {
        CourseSession session = null;

        Bundle extras = intent.getExtras();
        if (extras != null) {
            byte[] bytes = (byte[]) extras.get("session");
            session = ParcelableUtils.toParcelable(bytes, CourseSession.CREATOR);
        }

        return session;
    }
}
