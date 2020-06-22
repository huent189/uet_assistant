package vnu.uet.mobilecourse.assistant.alarm.receiver;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.firestore.util.Util;

import java.util.Locale;

import vnu.uet.mobilecourse.assistant.alarm.scheduler.SubmissionScheduler;
import vnu.uet.mobilecourse.assistant.model.event.CourseSubmissionEvent;
import vnu.uet.mobilecourse.assistant.model.notification.Notification_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.notification.SubmissionNotification;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.util.ParcelableUtils;

public class SubmissionReceiver extends SchedulerReceiver<CourseSubmissionEvent> {

    @SuppressLint("RestrictedApi")
    @Override
    protected Notification_UserSubCol generateNotification(CourseSubmissionEvent event) {
        String title = event.getTitle();

        String desc = String.format(
                Locale.ROOT,
                "Hoạt động sẽ diễn ra lúc %s, nằm trong khóa học %s",
                DateTimeUtils.DATE_TIME_FORMAT.format(event.getTime()),
                event.getCategory()
        );

        SubmissionNotification notificationDoc = new SubmissionNotification();
        notificationDoc.setId(Util.autoId());
        notificationDoc.setTitle(title);
        notificationDoc.setDescription(desc);
        notificationDoc.setNotifyTime(event.getTime().getTime() / 1000);
        notificationDoc.setCourseCode(event.getCategory());
        notificationDoc.setCourseId(event.getCourseId());
        notificationDoc.setMaterialId(event.getMaterialId());
        notificationDoc.setMaterialType(event.getType());

        return notificationDoc;
    }

    @Override
    protected String getAction() {
        return SubmissionScheduler.ACTION;
    }

    @Override
    protected CourseSubmissionEvent build(Intent intent) {
        CourseSubmissionEvent submission = null;

        Bundle extras = intent.getExtras();
        if (extras != null) {
            byte[] bytes = (byte[]) extras.get("submission");
            submission = ParcelableUtils.toParcelable(bytes, CourseSubmissionEvent.CREATOR);
        }

        return submission;
    }
}
