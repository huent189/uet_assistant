package vnu.uet.mobilecourse.assistant.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.repository.UserRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;

public class LoginActivity extends AppCompatActivity {

    private EditText etStudentId;

    private EditText etPassword;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etStudentId = findViewById(R.id.etStudentId);
        etPassword = findViewById(R.id.etPassword);
        progressBar = findViewById(R.id.progressBar);
    }

    public void login(View view) {
        String studentId = etStudentId.getText().toString();
        String password = etPassword.getText().toString();

        UserRepository userRepo = new UserRepository();
        StateLiveData<String> res = userRepo.makeLoginRequest(studentId, password);

        res.observe(LoginActivity.this, stateModel -> {
            switch (stateModel.getStatus()) {
                case SUCCESS:
                    Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    navigateToMyCourses();
                    break;

                case ERROR:
                    Toast.makeText(LoginActivity.this, "Login Failure", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    break;

                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }

    private void navigateToMyCourses() {
        Intent intent = new Intent(LoginActivity.this, MyCoursesActivity.class);
        startActivity(intent);
    }
}
