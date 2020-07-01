package vnu.uet.mobilecourse.assistant.alarm.scheduler;

import android.content.Context;
import android.content.Intent;

import vnu.uet.mobilecourse.assistant.alarm.receiver.SubmissionReceiver;
import vnu.uet.mobilecourse.assistant.model.event.CourseSubmissionEvent;
import vnu.uet.mobilecourse.assistant.util.ParcelableUtils;
import vnu.uet.mobilecourse.assistant.util.StringConst;

public class SubmissionScheduler extends Scheduler<CourseSubmissionEvent> {

    public static final String ACTION = "REMIND_SUBMISSION";

    private static SubmissionScheduler instance;

    public static SubmissionScheduler getInstance(Context context) {
        if (instance  == null) {
            instance = new SubmissionScheduler(context);
        }

        return instance;
    }

    private SubmissionScheduler(Context context) {
        super(context);
    }

    @Override
    protected Intent buildIntent(CourseSubmissionEvent event) {
        Intent intent = new Intent(mContext, SubmissionReceiver.class);
        intent.setAction(ACTION);
        intent.putExtra("submission", ParcelableUtils.toBytes(event));
        return intent;
    }

    @Override
    protected int getRequestCode(CourseSubmissionEvent event) {
        String id = event.getCategory()
                + StringConst.UNDERSCORE_CHAR + event.getCourseId()
                + StringConst.UNDERSCORE_CHAR + event.getMaterialId();

        return id.hashCode();
    }

    @Override
    protected long getTimeInMillis(CourseSubmissionEvent event) {
        return event.getTime().getTime();
    }

    @Override
    public void schedule(CourseSubmissionEvent event) {
        scheduleOneTime(event);
    }
}
