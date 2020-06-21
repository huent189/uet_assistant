package vnu.uet.mobilecourse.assistant.viewmodel.time;

import android.app.AlarmManager;
import android.os.CountDownTimer;

import androidx.lifecycle.LiveData;

public class TimeLiveData extends LiveData<Long> {

    private static final long INTERVAL = 1000;
    private static final long LIMIT = AlarmManager.INTERVAL_DAY;

    public TimeLiveData() {
        // first time
        postValue(System.currentTimeMillis());

        CountDownTimer timer = new CountDownTimer(LIMIT, INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                postValue(System.currentTimeMillis());
            }

            @Override
            public void onFinish() {
                postValue(null);
            }
        };

        timer.start();
    }
}
