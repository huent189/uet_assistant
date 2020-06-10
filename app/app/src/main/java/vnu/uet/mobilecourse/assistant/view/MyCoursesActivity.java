package vnu.uet.mobilecourse.assistant.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.database.DAO.CourseInfoDAO;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseInfo;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseSession;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.repository.firebase.TodoRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.work.remindHandler.CourseHandler;
import vnu.uet.mobilecourse.assistant.work.remindHandler.TodoHandler;

public class MyCoursesActivity extends AppCompatActivity {

    private NavController mNavController;

    private BottomNavigationView mNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_courses);

        mNavView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_courses, R.id.navigation_explore_course, R.id.navigation_chat, R.id.navigation_calendar)
//                .build();
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(mNavView, mNavController);

        // hide bottom navigator in chat fragment
        mNavController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            switch (destination.getId()) {
                case R.id.navigation_chat_room:
                    hideBottomNavigator();
                    break;

                default:
                    showBottomNavigator();
                    break;
            }
        });

        setupCourseReminders();
        setupTodoReminders();
    }

    private void setupCourseReminders() {
        new CourseInfoDAO().readAll().observe(MyCoursesActivity.this, new Observer<StateModel<List<CourseInfo>>>() {
            @Override
            public void onChanged(StateModel<List<CourseInfo>> stateModel) {
                switch (stateModel.getStatus()) {
                    case SUCCESS:
                        List<CourseInfo> courses = stateModel.getData();

                        courses.forEach(course -> {
                            List<CourseSession> sessions = course.getSessions();

                            sessions.forEach(session -> {
                                CourseHandler.getInstance().schedule(getApplicationContext(), session);
                            });
                        });

                        break;

                    case ERROR:
                        final String msg = "Không thể lên lịch báo thức giờ học";
                        Toast.makeText(MyCoursesActivity.this, msg, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void setupTodoReminders() {
        TodoRepository.getInstance().getAllTodos().observe(MyCoursesActivity.this, new Observer<StateModel<List<Todo>>>() {
            @Override
            public void onChanged(StateModel<List<Todo>> stateModel) {
                switch (stateModel.getStatus()) {
                    case SUCCESS:
                        stateModel.getData().forEach(todo -> {
                            long deadline = todo.getDeadline() * 1000;

                            if (deadline > System.currentTimeMillis()) {
                                TodoHandler.getInstance().schedule(getApplicationContext(), todo);
                            }
                        });

                        break;

                    case ERROR:
                        final String msg = "Không thể lên lịch báo thức công việc";
                        Toast.makeText(MyCoursesActivity.this, msg, Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });
    }

    private void hideBottomNavigator() {
        mNavView.setVisibility(View.GONE);
    }

    private void showBottomNavigator() {
        mNavView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        mNavController.navigateUp();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//
//        int count = fragmentManager.getBackStackEntryCount();
//
//        if (count == 0) {
//            super.onBackPressed();
//
//        } else {
//            fragmentManager.popBackStack();
//        }
    }
}
