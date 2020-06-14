package vnu.uet.mobilecourse.assistant.alarm.scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public abstract class Scheduler<T extends Parcelable> {

    protected Context mContext;

    private AlarmManager mAlarmManager;

    public Scheduler(Context context) {
        mContext = context.getApplicationContext();
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    protected abstract Intent buildIntent(T model);

    protected abstract int getRequestCode(T model);

    protected abstract long getTimeInMillis(T model);

    public abstract void schedule(T model);

    public void cancel(T model) {
        Intent intent = buildIntent(model);

        int requestCode = getRequestCode(model);

        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(mContext, requestCode, intent, FLAG_CANCEL_CURRENT);

        mAlarmManager.cancel(pendingIntent);
    }

    protected void scheduleOneTime(T model) {
        Intent intent = buildIntent(model);

        int requestCode = getRequestCode(model);
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(mContext, requestCode, intent, FLAG_CANCEL_CURRENT);

        long time = getTimeInMillis(model);

        // schedule if after current time
        if (time > System.currentTimeMillis()) {
            mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }
    }

    protected void scheduleInterval(T model, long interval) {
        Intent intent = buildIntent(model);

        int requestCode = getRequestCode(model);
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(mContext, requestCode, intent, FLAG_CANCEL_CURRENT);

        long time = getTimeInMillis(model);

        // schedule if after current time
        if (time > System.currentTimeMillis()) {
            mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, interval, pendingIntent);
        }
    }
}
