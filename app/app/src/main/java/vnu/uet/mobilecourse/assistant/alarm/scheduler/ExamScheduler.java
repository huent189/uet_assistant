package vnu.uet.mobilecourse.assistant.alarm.scheduler;

import android.content.Context;
import android.content.Intent;

import vnu.uet.mobilecourse.assistant.alarm.receiver.ExamReceiver;
import vnu.uet.mobilecourse.assistant.model.FinalExam;
import vnu.uet.mobilecourse.assistant.util.ParcelableUtils;

public class ExamScheduler extends Scheduler<FinalExam> {
    public static final String ACTION = "REMIND_FINAL_EXAM";
    public static final int REMIND_BEFORE_EXAM = 15 * 60 * 1000;
    private static ExamScheduler instance;
    public static ExamScheduler getInstance(Context context){
        if (instance == null){
            instance = new ExamScheduler(context);
        }
        return instance;
    }
    private ExamScheduler(Context context) {
        super(context);
    }

    @Override
    protected Intent buildIntent(FinalExam model) {
        Intent intent = new Intent(mContext, ExamReceiver.class);
        intent.setAction(ACTION);
        intent.putExtra("exam", ParcelableUtils.toBytes(model));
        return intent;
    }

    @Override
    protected int getRequestCode(FinalExam model) {
        return model.getClassCode().hashCode();
    }

    @Override
    protected long getTimeInMillis(FinalExam model) {
        return model.getExamTime() - REMIND_BEFORE_EXAM;
    }

    @Override
    public void schedule(FinalExam model) {
        scheduleOneTime(model);
    }
}
