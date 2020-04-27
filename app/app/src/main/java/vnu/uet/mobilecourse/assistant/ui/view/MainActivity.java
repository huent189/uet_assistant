package vnu.uet.mobilecourse.assistant.ui.view;

import androidx.appcompat.app.AppCompatActivity;
import vnu.uet.mobilecourse.assistant.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import vnu.uet.mobilecourse.assistant.ui.model.Token;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Token.init(getApplicationContext());
    }

    public void accessCourses(View view) {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
