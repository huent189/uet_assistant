package vnu.uet.mobilecourse.assistant.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.repository.UserRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;

public class LoginActivity extends AppCompatActivity {

    private EditText etStudentId;

    private EditText etPassword;

    private ProgressBar progressBar;

    private TextView tvLoadingTitle;

    private TextView tvVerifyMailTitle;

    private Button btnLogin;

    private boolean loginSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etStudentId = findViewById(R.id.etStudentId);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        tvLoadingTitle = findViewById(R.id.tvLoadingTitle);
        progressBar = findViewById(R.id.progressBar);

        tvVerifyMailTitle = findViewById(R.id.tvVerifyMailTitle);
    }

    public void login(View view) {
        String studentId = etStudentId.getText().toString();
        String password = etPassword.getText().toString();

        UserRepository userRepo = new UserRepository();
        StateLiveData<String> res = userRepo.makeLoginRequest(studentId, password);

        res.observe(LoginActivity.this, stateModel -> {
            switch (stateModel.getStatus()) {
                case SUCCESS:
                    if (!loginSuccess) {
                        loginSuccess = true;
//                        Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
//                        navigateToMyCourses();

                        tvLoadingTitle.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);

                        etStudentId.setVisibility(View.INVISIBLE);
                        etPassword.setVisibility(View.INVISIBLE);
                        btnLogin.setVisibility(View.INVISIBLE);

                        tvVerifyMailTitle.setVisibility(View.VISIBLE);
                    }
                    break;

                case ERROR:
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();

                    tvLoadingTitle.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);

                    etStudentId.setVisibility(View.VISIBLE);
                    etPassword.setVisibility(View.VISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);

                    tvVerifyMailTitle.setVisibility(View.INVISIBLE);

                    break;

                case LOADING:
                    tvLoadingTitle.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);

                    etStudentId.setVisibility(View.INVISIBLE);
                    etPassword.setVisibility(View.INVISIBLE);
                    btnLogin.setVisibility(View.INVISIBLE);

                    tvVerifyMailTitle.setVisibility(View.INVISIBLE);

                    break;
            }
        });
    }

    private void navigateToMyCourses() {
        Intent intent = new Intent(LoginActivity.this, MyCoursesActivity.class);
        startActivity(intent);
    }
}
