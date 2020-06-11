package vnu.uet.mobilecourse.assistant.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.SharedPreferencesManager;
import vnu.uet.mobilecourse.assistant.database.DAO.CourseInfoDAO;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseInfo;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseSession;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.repository.FirebaseAuthenticationService;
import vnu.uet.mobilecourse.assistant.repository.course.UserRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.TodoRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.work.remindHandler.CourseHandler;
import vnu.uet.mobilecourse.assistant.work.remindHandler.TodoHandler;


public class LoginFirebaseActivity extends AppCompatActivity {

    private ViewGroup mLayoutVerifySuccess;
    private ViewGroup mLayoutVerifyFail;
    private ViewGroup mLayoutVerifying;

//    private boolean remindCourse = false, remindTodo = false;

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
                                    .getStringValue(SharedPreferencesManager.REGISTER_EMAIL)
                            , deepLink
                    );

            loginFirebase.observe(this, loginState -> {
                switch (loginState.getStatus()) {
                    case SUCCESS:
                        showSuccessLayout();
                        navigateToMyCourses();
//                        setupTodoReminders();
//                        setupCourseReminders();
                        break;

                    case ERROR:
                        showErrorLayout();
                        break;
                }
            });
        }
    }

//    private static final String TAG = LoginFirebaseActivity.class.getName();
//
//    private void setupCourseReminders() {
//        new CourseInfoDAO().readAll().observe(LoginFirebaseActivity.this, new Observer<StateModel<List<CourseInfo>>>() {
//            @Override
//            public void onChanged(StateModel<List<CourseInfo>> stateModel) {
//                switch (stateModel.getStatus()) {
//                    case SUCCESS:
//                        remindCourse = true;
//
//                        List<CourseInfo> courses = stateModel.getData();
//
//                        courses.forEach(course -> {
//                            List<CourseSession> sessions = course.getSessions();
//
//                            sessions.forEach(session -> {
//                                Log.d(TAG, session.toString());
//                                CourseHandler.getInstance().schedule(getApplicationContext(), session);
//                            });
//                        });
//
//                        if (remindTodo && remindCourse) {
//                            navigateToMyCourses();
//                        }
//
//                        break;
//
//                    case ERROR:
//                        showErrorLayout();
//                        break;
//                }
//            }
//        });
//    }
//
//    private void setupTodoReminders() {
//        TodoRepository.getInstance().getAllTodos().observe(LoginFirebaseActivity.this, new Observer<StateModel<List<Todo>>>() {
//            @Override
//            public void onChanged(StateModel<List<Todo>> stateModel) {
//                switch (stateModel.getStatus()) {
//                    case SUCCESS:
//                        remindTodo = true;
//
//                        stateModel.getData().forEach(todo -> {
//                            long deadline = todo.getDeadline() * 1000;
//
//                            if (deadline > System.currentTimeMillis()) {
//                                TodoHandler.getInstance().schedule(getApplicationContext(), todo);
//                            }
//                        });
//
//                        if (remindTodo && remindCourse) {
//                            navigateToMyCourses();
//                        }
//
//                        break;
//
//                    case ERROR:
//                        showErrorLayout();
//                        break;
//                }
//
//            }
//        });
//    }

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
