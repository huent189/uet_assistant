package vnu.uet.mobilecourse.assistant.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.SharedPreferencesManager;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.repository.FirebaseAuthenticationService;
import vnu.uet.mobilecourse.assistant.repository.UserRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;


public class LoginFirebaseActivity extends AppCompatActivity {

    private ConstraintLayout layoutVerifySuccess;

    private ConstraintLayout layoutVerifyFail;

    private LinearLayout layoutVerifying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_firebase);

        layoutVerifySuccess = findViewById(R.id.layoutVerifySuccess);
        layoutVerifyFail = findViewById(R.id.layoutVerifyFail);
        layoutVerifying = findViewById(R.id.layoutVerifying);

        String deepLink = getIntent().getDataString();

        // Case: login course but not login fb
        if (deepLink == null)
            showErrorLayout();
        else {
            StateLiveData<String> loginFirebase = new FirebaseAuthenticationService()
                    .signInWithEmailLink(
                            SharedPreferencesManager
                                    .getValue(SharedPreferencesManager.REGISTER_EMAIL)
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
        layoutVerifySuccess.setVisibility(View.VISIBLE);
        layoutVerifyFail.setVisibility(View.GONE);
        layoutVerifying.setVisibility(View.GONE);
    }

    private void showErrorLayout() {
        layoutVerifySuccess.setVisibility(View.GONE);
        layoutVerifyFail.setVisibility(View.VISIBLE);
        layoutVerifying.setVisibility(View.GONE);
    }

    private void navigateToMyCourses() {
        Intent intent = new Intent(this, MyCoursesActivity.class);
        startActivity(intent);
    }

    public void sendMail(View view) {
        new UserRepository().resendVerificationMail();
    }
}
