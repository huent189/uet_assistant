package vnu.uet.mobilecourse.assistant.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.SharedPreferencesManager;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.repository.FirebaseAuthenticationService;
import vnu.uet.mobilecourse.assistant.repository.UserRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.TodoRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.work.TodoReminder;


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
                    .signInWithEmailLink(
                            SharedPreferencesManager
                                    .getValue(SharedPreferencesManager.REGISTER_EMAIL)
                            , deepLink
                    );

            loginFirebase.observe(this, loginState -> {
                switch (loginState.getStatus()) {
                    case SUCCESS:
                        showSuccessLayout();
                        setupReminders();
                        break;

                    case ERROR:
                        showErrorLayout();
                        break;
                }
            });
        }
    }

    private void setupReminders() {
        TodoRepository.getInstance().getAllTodos().observe(LoginFirebaseActivity.this, new Observer<StateModel<List<Todo>>>() {
            @Override
            public void onChanged(StateModel<List<Todo>> stateModel) {
                switch (stateModel.getStatus()) {
                    case SUCCESS:
                        stateModel.getData().forEach(todo -> {
                            long deadline = todo.getDeadline() * 1000;

                            if (deadline > System.currentTimeMillis()) {
                                scheduleReminder(todo);
                            }
                        });

                        navigateToMyCourses();

                        break;

                    case ERROR:
                        showErrorLayout();
                        break;
                }

            }
        });
    }


    private void scheduleReminder(Todo todo) {
        long delayTime = todo.getDeadline() * 1000 - System.currentTimeMillis();

        Data inputData = new Data.Builder()
                .putString("id", todo.getId())
                .putString("title", todo.getTitle())
                .putString("description", todo.getDescription())
                .putString("todoList", todo.getTodoListId())
                .build();

        OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(TodoReminder.class)
                .setInitialDelay(delayTime, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag(todo.getId())
                .build();

        WorkManager.getInstance(getApplicationContext()).enqueue(notificationWork);
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
