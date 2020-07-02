package vnu.uet.mobilecourse.assistant.work.courses;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.google.firebase.firestore.util.Util;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.alarm.scheduler.ExamScheduler;
import vnu.uet.mobilecourse.assistant.model.FinalExam;
import vnu.uet.mobilecourse.assistant.model.notification.FinalExamNotification;
import vnu.uet.mobilecourse.assistant.model.notification.Notification_UserSubCol;
import vnu.uet.mobilecourse.assistant.repository.course.PortalRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.NavigationBadgeRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.NotificationRepository;
import vnu.uet.mobilecourse.assistant.util.NotificationHelper;

import java.text.ParseException;
import java.util.List;

public class SyncFinalExamWorker extends Worker {
    private PortalRepository portalRepository;
    private Context mContext;
    private static final String CHANNEL_ID = FinalExam.class.getSimpleName();
    public SyncFinalExamWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
        portalRepository = new PortalRepository();
    }
    @SuppressLint("RestrictedApi")
    private Notification_UserSubCol generateNotification(List<FinalExam> exams){
        FinalExamNotification notification = new FinalExamNotification();
        notification.setId(Util.autoId());
        notification.setTitle("Lịch thi của bạn đã được cập nhật");
        StringBuilder desc = new StringBuilder("Lịch thi các môn học sau đã được thay đổi");
        for(FinalExam exam : exams){
            desc.append("\n"+exam.getClassName());
        }
        notification.setDescription(desc.toString());
        notification.setNotifyTime(System.currentTimeMillis() / 1000);
        return notification;
    }
    @NonNull
    @Override
    public Result doWork() {
        try {
            portalRepository.syncFinalExams();
            List<FinalExam> updateList = portalRepository.syncFinalExams();
            if(updateList == null || updateList.isEmpty()){
                return Result.success();
            }
            Notification_UserSubCol notification = generateNotification(updateList);
            NotificationRepository.getInstance().add(notification);
            NavigationBadgeRepository.getInstance().increaseNewNotifications();
            pushNotification(mContext, notification);
            updateList.forEach(exam -> ExamScheduler.getInstance(mContext).schedule(exam));
        } catch (ParseException e) {
            e.printStackTrace();
            return Result.failure();
        }
        return Result.success();
    }
    private void pushNotification(Context context, Notification_UserSubCol instance){
        NotificationHelper helper = NotificationHelper.getsInstance();
        Notification notification = helper.build(context, CHANNEL_ID,
                R.drawable.ic_check_circle_24dp, instance.getTitle(), instance.getDescription());
        helper.notify(context, instance.getId(), notification);
    }
}
