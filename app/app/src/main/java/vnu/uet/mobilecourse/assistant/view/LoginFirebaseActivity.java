package vnu.uet.mobilecourse.assistant.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import vnu.uet.mobilecourse.assistant.R;


public class LoginFirebaseActivity extends AppCompatActivity {

    private String deepLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_firebase);
        TextView MyDebugText = findViewById(R.id.DebugText);

    }

}
