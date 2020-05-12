package vnu.uet.mobilecourse.assistant.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.SharedPreferencesManager;
import vnu.uet.mobilecourse.assistant.repository.FirebaseAuthenticationService;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;


public class LoginFirebaseActivity extends AppCompatActivity {

    private String deepLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_firebase);

        deepLink = getIntent().getDataString();

        TextView myDebugText = findViewById(R.id.DebugText);
        StateLiveData<String> loginFirebase = new FirebaseAuthenticationService().signInWithEmailLink(SharedPreferencesManager.getValue(SharedPreferencesManager.REGISTER_EMAIL), deepLink);
        loginFirebase.observe(this, loginState -> {
            switch (loginState.getStatus()) {
                case SUCCESS: navigateToMyCourses(); break;
                case ERROR: myDebugText.setText(loginState.getError().toString()); break;
            }
        });
    }

    private void navigateToMyCourses() {
        Intent intent = new Intent(this, MyCoursesActivity.class);
        startActivity(intent);
    }

}
