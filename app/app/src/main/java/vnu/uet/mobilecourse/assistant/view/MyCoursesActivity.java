package vnu.uet.mobilecourse.assistant.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.SharedPreferencesManager;
import vnu.uet.mobilecourse.assistant.database.DAO.CourseInfoDAO;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseInfo;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseSession;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.model.firebase.User;
import vnu.uet.mobilecourse.assistant.repository.firebase.FirebaseUserRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.NavigationBadgeRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.TodoRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;
import vnu.uet.mobilecourse.assistant.work.remindHandler.CourseHandler;
import vnu.uet.mobilecourse.assistant.work.remindHandler.TodoHandler;

public class MyCoursesActivity extends AppCompatActivity {

    private NavController mNavController;

    private BottomNavigationView mNavView;

//    private SharedPreferences.OnSharedPreferenceChangeListener mOnSharedPrefChangeListener;

    private View mNotificationBadge;

    private NavigationBadgeRepository mNavigationBadgeRepo = NavigationBadgeRepository.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_courses);

        mNavView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(mNavView, mNavController);

        setupNavigationBadges();

        // hide bottom navigator in chat fragment
        mNavController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            switch (destination.getId()) {
                case R.id.navigation_chat_room:
                    hideBottomNavigator();
                    break;

                case R.id.navigation_notifications:
//                    SharedPreferencesManager.setInt(SharedPreferencesManager.NEW_NOTIFICATION, 0);
                    mNavigationBadgeRepo.seeAllNotifications();
                    break;

                default:
                    showBottomNavigator();
                    break;
            }
        });

//        mOnSharedPrefChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
//            @Override
//            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//                if (key.equals(SharedPreferencesManager.NEW_NOTIFICATION)) {
//                    updateNotificationBadge();
//                }
//            }
//        };

        mNavigationBadgeRepo.getNewNotifications().observe(this, stateModel -> {
            int counter = 0;

            if (stateModel.getStatus() == StateStatus.SUCCESS) {
                counter = stateModel.getData();
            }

            updateNotificationBadge(counter);
        });

        setupCourseReminders();
        setupTodoReminders();
    }

    private void setupNavigationBadges() {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) mNavView.getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(1);

        mNotificationBadge = LayoutInflater.from(this).inflate(R.layout.layout_bottom_nav_badge, menuView, false);
        itemView.addView(mNotificationBadge);
    }

    private void updateNotificationBadge(int counter) {
//        int counter = SharedPreferencesManager.getInt(SharedPreferencesManager.NEW_NOTIFICATION);

        if (counter != 0) {
            TextView tvCounter = mNotificationBadge.findViewById(R.id.tvCounter);
            tvCounter.setText(String.valueOf(counter * 10));
            mNotificationBadge.setVisibility(View.VISIBLE);
        } else {
            mNotificationBadge.setVisibility(View.GONE);
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        SharedPreferencesManager.registerOnChangeListener(mOnSharedPrefChangeListener);
//    }

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
