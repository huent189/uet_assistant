package vnu.uet.mobilecourse.assistant.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.repository.FirebaseAuthenticationService;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;


public class LoginFirebaseActivity extends AppCompatActivity {

    private String deepLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_login);
        TextView MyDebugText = findViewById(R.id.DebugText);

    }

}
