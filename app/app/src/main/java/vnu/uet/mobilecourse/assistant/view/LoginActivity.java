package vnu.uet.mobilecourse.assistant.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.repository.course.UserRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;

public class LoginActivity extends AppCompatActivity {

    private EditText mEtStudentId;
    private EditText mEtPassword;
    private ProgressBar mProgressBar;
    private TextView mTvLoadingTitle;
    private TextView mTvVerifyMailTitle;
    private Button mBtnLogin;
    private boolean mLoginSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEtStudentId = findViewById(R.id.etStudentId);
        mEtPassword = findViewById(R.id.etPassword);
        mBtnLogin = findViewById(R.id.btnLogin);

        mTvLoadingTitle = findViewById(R.id.tvLoadingTitle);
        mProgressBar = findViewById(R.id.progressBar);

        mTvVerifyMailTitle = findViewById(R.id.tvVerifyMailTitle);
    }

    public void login(View view) {
        String studentId = mEtStudentId.getText().toString();
        String password = mEtPassword.getText().toString();

        UserRepository userRepo = new UserRepository();
        StateLiveData<String> res = userRepo.makeLoginRequest(studentId, password);

        res.observe(LoginActivity.this, stateModel -> {
            switch (stateModel.getStatus()) {
                case SUCCESS:
                    if (!mLoginSuccess) {
                        mLoginSuccess = true;

                        mTvLoadingTitle.setVisibility(View.INVISIBLE);
                        mProgressBar.setVisibility(View.INVISIBLE);

                        mEtStudentId.setVisibility(View.INVISIBLE);
                        mEtPassword.setVisibility(View.INVISIBLE);
                        mBtnLogin.setVisibility(View.INVISIBLE);

                        mTvVerifyMailTitle.setVisibility(View.VISIBLE);
                    }
                    break;

                case ERROR:
                    Toast.makeText(LoginActivity.this, LOGIN_FAILURE, Toast.LENGTH_SHORT).show();

                    mTvLoadingTitle.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);

                    mEtStudentId.setVisibility(View.VISIBLE);
                    mEtPassword.setVisibility(View.VISIBLE);
                    mBtnLogin.setVisibility(View.VISIBLE);

                    mTvVerifyMailTitle.setVisibility(View.INVISIBLE);

                    break;

                case LOADING:
                    mTvLoadingTitle.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);

                    mEtStudentId.setVisibility(View.INVISIBLE);
                    mEtPassword.setVisibility(View.INVISIBLE);
                    mBtnLogin.setVisibility(View.INVISIBLE);

                    mTvVerifyMailTitle.setVisibility(View.INVISIBLE);

                    break;
            }
        });
    }

    private static final String LOGIN_FAILURE = "Đăng nhập thất bại";
}
