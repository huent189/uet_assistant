package vnu.uet.mobilecourse.assistant.alarm.scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;

public abstract class Scheduler<T> {

    private static final String TAG = Scheduler.class.getName();

    protected Context mContext;

    private AlarmManager mAlarmManager;

    protected Scheduler(Context context) {
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
        Log.d(TAG, "Build intent success: " + intent);

        int requestCode = getRequestCode(model);
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(mContext, requestCode, intent, FLAG_CANCEL_CURRENT);
        Log.d(TAG, "Extract pending intent success: " + pendingIntent + " with request code " + requestCode);

        long time = getTimeInMillis(model);

        // schedule if after current time
        if (time > System.currentTimeMillis()) {
            mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
            Log.d(TAG, "schedule one time start at " + time + " millis");
        }
    }

    protected void scheduleInterval(T model, long interval) {
        Intent intent = buildIntent(model);
        Log.d(TAG, "Build intent success: " + intent);

        int requestCode = getRequestCode(model);
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(mContext, requestCode, intent, FLAG_CANCEL_CURRENT);
        Log.d(TAG, "Extract pending intent success: " + pendingIntent + " with request code " + requestCode);

        long time = getTimeInMillis(model);

        // schedule if after current time
        if (time > System.currentTimeMillis()) {
            mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, interval, pendingIntent);
            Log.d(TAG, "schedule repeat start at " + time + " millis and interval " + interval);
        }
    }
}
