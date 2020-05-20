package vnu.uet.mobilecourse.assistant.view;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.receiver.AlarmReceiver;
import vnu.uet.mobilecourse.assistant.repository.FirebaseAuthenticationService;
import vnu.uet.mobilecourse.assistant.repository.UserRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void accessCourses(View view) {
        new UserRepository().isLoggedIn().observe(MainActivity.this, new Observer<StateModel<String>>() {
            @Override
            public void onChanged(StateModel<String> stateModel) {
                switch (stateModel.getStatus()) {
                    case SUCCESS:
                        boolean isFirebaseLoggedIn = FirebaseAuthenticationService.isFirebaseLoggedIn();

                        if (isFirebaseLoggedIn)
                            navigateToActivity(MyCoursesActivity.class);
                        else
                            navigateToActivity(LoginFirebaseActivity.class);

//                        testAlarm();

                        break;

                    case ERROR:
                        stateModel.getError().printStackTrace();
                        navigateToActivity(LoginActivity.class);
                        break;

                }
            }
        });
    }

    private void navigateToActivity(Class<? extends AppCompatActivity> activity) {
        Intent intent = new Intent(MainActivity.this, activity);
        startActivity(intent);
    }

    private void testAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Context context = getApplicationContext();

        Intent intent = new Intent(context, AlarmReceiver.class);

        intent.setAction("remind");
        intent.putExtra("data", "test");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        long ALARM_DELAY_IN_SECOND = 50;
        long alarmTimeUTC = System.currentTimeMillis() + ALARM_DELAY_IN_SECOND;

        assert alarmManager != null;
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeUTC, "pendingIntent", new AlarmManager.OnAlarmListener() {
            @Override
            public void onAlarm() {
                Toast.makeText(context, "data", Toast.LENGTH_LONG).show();
            }
        }, null);
    }
}
