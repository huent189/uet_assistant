package vnu.uet.mobilecourse.assistant.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.repository.UserRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText etStudentId;

    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etStudentId = findViewById(R.id.etStudentId);
        etPassword = findViewById(R.id.etPassword);
    }

    public void login(View view) {
        String studentId = etStudentId.getText().toString();
        String password = etPassword.getText().toString();

        UserRepository userRepo = new UserRepository();
        StateLiveData<String> res = userRepo.makeLoginRequest(studentId, password);

        res.observe(LoginActivity.this, new Observer<StateModel<String>>() {
            @Override
            public void onChanged(StateModel<String> stringStateModel) {
                switch (stringStateModel.getStatus()) {
                    case SUCCESS:
                        Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, MyCoursesActivity.class);
                        startActivity(intent);
                        break;

                    case ERROR:
                        Toast.makeText(LoginActivity.this, "Login Failure", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

//        Intent intent = new Intent(LoginActivity.this, MyCoursesActivity.class);
//        startActivity(intent);

        System.out.println();
    }
}
