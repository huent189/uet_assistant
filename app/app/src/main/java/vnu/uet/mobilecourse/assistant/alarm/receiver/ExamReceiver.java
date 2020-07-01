package vnu.uet.mobilecourse.assistant.alarm.receiver;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.firestore.util.Util;
import vnu.uet.mobilecourse.assistant.alarm.scheduler.ExamScheduler;
import vnu.uet.mobilecourse.assistant.model.FinalExam;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseSession;
import vnu.uet.mobilecourse.assistant.model.notification.FinalExamNotification;
import vnu.uet.mobilecourse.assistant.model.notification.Notification_UserSubCol;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.util.ParcelableUtils;

public class ExamReceiver extends SchedulerReceiver<FinalExam> {

    @SuppressLint("RestrictedApi")
    @Override
    protected Notification_UserSubCol generateNotification(FinalExam model) {
        FinalExamNotification notification = new FinalExamNotification();
        notification.setId(Util.autoId());
        notification.setTitle("Thi cuối kỳ: " + model.getClassName());
        notification.setDescription("Số báo danh: " + model.getIdNumber()
                                    + "\nGiờ thi : " + DateTimeUtils.DATE_TIME_FORMAT.format(model.getExamTime())
                                    + "\nĐịa điểm: " + model.getPlace());
        notification.setNotifyTime(model.getExamTime() - ExamScheduler.REMIND_BEFORE_EXAM);
        return notification;
    }

    @Override
    protected String getAction() {
        return ExamScheduler.ACTION;
    }

    @Override
    protected FinalExam build(Intent intent) {
        FinalExam exam = null;

        Bundle extras = intent.getExtras();
        if (extras != null) {
            byte[] bytes = (byte[]) extras.get("exam");
            exam = ParcelableUtils.toParcelable(bytes, FinalExam.CREATOR);
        }

        return exam;
    }
}
