package vnu.uet.mobilecourse.assistant.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.SharedPreferencesManager;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.repository.FirebaseAuthenticationService;
import vnu.uet.mobilecourse.assistant.repository.course.UserRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;


public class LoginFirebaseActivity extends AppCompatActivity {

    private ViewGroup mLayoutVerifySuccess;
    private ViewGroup mLayoutVerifyFail;
    private ViewGroup mLayoutVerifying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_firebase);

        mLayoutVerifySuccess = findViewById(R.id.layoutVerifySuccess);
        mLayoutVerifyFail = findViewById(R.id.layoutVerifyFail);
        mLayoutVerifying = findViewById(R.id.layoutVerifying);

        String deepLink = getIntent().getDataString();

        // Case: login course but not login fb
        if (deepLink == null)
            showErrorLayout();
        else {
            StateLiveData<String> loginFirebase = new FirebaseAuthenticationService()
                    .signInWithEmailLink(User.getInstance().getEmail()
//                            SharedPreferencesManager
//                                    .getStringValue(User.REGISTER_EMAIL)
                            , deepLink
                    );

            loginFirebase.observe(this, loginState -> {
                switch (loginState.getStatus()) {
                    case SUCCESS:
                        showSuccessLayout();
                        navigateToMyCourses();
                        break;

                    case ERROR:
                        showErrorLayout();
                        break;
                }
            });
        }
    }

    private void showSuccessLayout() {
        mLayoutVerifySuccess.setVisibility(View.VISIBLE);
        mLayoutVerifyFail.setVisibility(View.GONE);
        mLayoutVerifying.setVisibility(View.GONE);
    }

    private void showErrorLayout() {
        mLayoutVerifySuccess.setVisibility(View.GONE);
        mLayoutVerifyFail.setVisibility(View.VISIBLE);
        mLayoutVerifying.setVisibility(View.GONE);
    }

    private void navigateToMyCourses() {
        Intent intent = new Intent(this, MyCoursesActivity.class);
        startActivity(intent);
    }

    public void sendMail(View view) {
        new UserRepository().resendVerificationMail();
    }
}
