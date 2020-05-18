package vnu.uet.mobilecourse.assistant.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import vnu.uet.mobilecourse.assistant.R;
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
}
