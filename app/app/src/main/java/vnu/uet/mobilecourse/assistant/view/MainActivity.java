package vnu.uet.mobilecourse.assistant.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.repository.FirebaseAuthenticationService;
import vnu.uet.mobilecourse.assistant.repository.UserRepository;

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

        new UserRepository().isLoggedIn().observe(MainActivity.this, stateModel -> {
            switch (stateModel.getStatus()) {
                case SUCCESS:
                    boolean isFirebaseLoggedIn = FirebaseAuthenticationService.isFirebaseLoggedIn();

                    if (isFirebaseLoggedIn) {
                        navigateToActivity(MyCoursesActivity.class);
                    } else {
                        navigateToActivity(LoginFirebaseActivity.class);
                    }

                    break;

                case ERROR:
                    stateModel.getError().printStackTrace();
                    navigateToActivity(LoginActivity.class);

                    break;
            }
        });
    }

    private void navigateToActivity(Class<? extends AppCompatActivity> activity) {
        Intent intent = new Intent(MainActivity.this, activity);
        startActivity(intent);
    }
}
