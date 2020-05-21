package vnu.uet.mobilecourse.assistant.view;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import vnu.uet.mobilecourse.assistant.R;

public class MainActivity extends AppCompatActivity {

    private Button mBtnAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnAccess = findViewById(R.id.btnAccess);
    }

    public void accessCourses(View view) {
        mBtnAccess.setActivated(false);
        navigateToActivity(MyCoursesActivity.class);


//        new UserRepository().isLoggedIn().observe(MainActivity.this, new Observer<StateModel<String>>() {
//            @Override
//            public void onChanged(StateModel<String> stateModel) {
//                switch (stateModel.getStatus()) {
//                    case SUCCESS:
//                        boolean isFirebaseLoggedIn = FirebaseAuthenticationService.isFirebaseLoggedIn();
//
//                        if (isFirebaseLoggedIn)
//                            navigateToActivity(MyCoursesActivity.class);
//                        else
//                            navigateToActivity(LoginFirebaseActivity.class);
//
//                        break;
//
//                    case ERROR:
//                        stateModel.getError().printStackTrace();
//                        navigateToActivity(LoginActivity.class);
//
//                        break;
//
//                }
//            }
//        });
    }

    private void navigateToActivity(Class<? extends AppCompatActivity> activity) {
        Intent intent = new Intent(MainActivity.this, activity);
        startActivity(intent);
    }
}
