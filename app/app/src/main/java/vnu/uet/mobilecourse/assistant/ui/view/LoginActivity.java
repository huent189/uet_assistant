package vnu.uet.mobilecourse.assistant.ui.view;

import androidx.appcompat.app.AppCompatActivity;
import vnu.uet.mobilecourse.assistant.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view) {
        Intent intent = new Intent(LoginActivity.this, MyCoursesActivity.class);
        startActivity(intent);
    }
}
