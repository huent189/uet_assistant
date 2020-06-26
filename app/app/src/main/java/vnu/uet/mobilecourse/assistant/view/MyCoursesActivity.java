package vnu.uet.mobilecourse.assistant.view;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
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
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.alarm.scheduler.SessionScheduler;
import vnu.uet.mobilecourse.assistant.alarm.scheduler.TodoScheduler;
import vnu.uet.mobilecourse.assistant.database.DAO.CourseInfoDAO;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseInfo;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseSession;
import vnu.uet.mobilecourse.assistant.repository.firebase.NavigationBadgeRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.TodoRepository;
import vnu.uet.mobilecourse.assistant.util.NetworkChangeReceiver;
import vnu.uet.mobilecourse.assistant.util.NetworkUtils;
import vnu.uet.mobilecourse.assistant.util.NotificationHelper;
import vnu.uet.mobilecourse.assistant.view.notification.NotificationsFragment;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;
import vnu.uet.mobilecourse.assistant.work.courses.CourseDataSynchronization;

public class MyCoursesActivity extends AppCompatActivity {

    private NavController mNavController;

    private BottomNavigationView mNavView;

    private View mNotificationBadge, mChatBadge;

    private NavigationBadgeRepository mNavigationBadgeRepo = NavigationBadgeRepository.getInstance();

    private NetworkChangeReceiver mNetworkListener = new NetworkChangeReceiver();

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
                    mNavigationBadgeRepo.seeAllNotifications();
                    break;

                default:
                    showBottomNavigator();
                    break;
            }
        });

        // observer counter badge
        mNavigationBadgeRepo.getNewNotifications().observe(this, stateModel -> {
            int counter = 0;

            if (stateModel.getStatus() == StateStatus.SUCCESS) {
                counter = stateModel.getData();
            }

            updateNotificationBadge(counter);
        });

        mNavigationBadgeRepo.getNonSeenChats().observe(this, stateModel -> {
            long counter = 0;

            if (stateModel.getStatus() == StateStatus.SUCCESS) {
                counter = stateModel.getData();
            }

            updateChatBadge(counter);
        });

        // schedule job
        setupCourseReminders();
        setupTodoReminders();
        CourseDataSynchronization.start();

        // check if open activity by notification
        checkIfOpenByNotification();

        // setup error text view
        setupErrorTextView();
    }

    private void setupErrorTextView() {
        TextView tvError = findViewById(R.id.tvError);

        boolean courseAvailable = getIntent()
                .getBooleanExtra("courseAvailable", true);

        if (!courseAvailable) {
            tvError.setVisibility(View.VISIBLE);
            tvError.setText(R.string.title_error_unavailable_host);
        }

        mNetworkListener.observe(MyCoursesActivity.this, status -> {
            if (courseAvailable) {
                if (status == NetworkUtils.STATUS_NOT_CONNECTED) {
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText(R.string.title_error_no_connectivity);
                } else {
                    tvError.setVisibility(View.GONE);
                }
            }
        });
    }


    private void checkIfOpenByNotification() {
        if (NotificationHelper.ACTION_OPEN.equals(getIntent().getAction())) {

            String menuFragment = getIntent().getStringExtra("fragment");

            if (menuFragment != null) {
                if (menuFragment.equals(NotificationsFragment.class.getName())) {
                    mNavController.navigate(R.id.action_navigation_courses_to_navigation_notifications);
                }
            }
        }
    }

    private void setupNavigationBadges() {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) mNavView.getChildAt(0);

        // Notification Badge
        BottomNavigationItemView notificationView = (BottomNavigationItemView) menuView.getChildAt(1);
        mNotificationBadge = LayoutInflater.from(this)
                .inflate(R.layout.layout_bottom_nav_badge, menuView, false);
        notificationView.addView(mNotificationBadge);

        // Chat Badge
        BottomNavigationItemView chatView = (BottomNavigationItemView) menuView.getChildAt(2);
        mChatBadge = LayoutInflater.from(this)
                .inflate(R.layout.layout_bottom_nav_badge, menuView, false);
        chatView.addView(mChatBadge);
    }


    private void updateNotificationBadge(int counter) {
        if (counter != 0) {
            TextView tvCounter = mNotificationBadge.findViewById(R.id.tvCounter);
            tvCounter.setText(String.valueOf(counter));
            mNotificationBadge.setVisibility(View.VISIBLE);
        } else {
            mNotificationBadge.setVisibility(View.GONE);
        }
    }

    private void updateChatBadge(long counter) {
        if (counter != 0) {
            TextView tvCounter = mChatBadge.findViewById(R.id.tvCounter);
            tvCounter.setText(String.valueOf(counter));
            mChatBadge.setVisibility(View.VISIBLE);
        } else {
            mChatBadge.setVisibility(View.GONE);
        }
    }

    private void setupCourseReminders() {
        new CourseInfoDAO().readAll().observe(MyCoursesActivity.this, stateModel -> {
            switch (stateModel.getStatus()) {
                case SUCCESS:
                    List<CourseInfo> courses = stateModel.getData();

                    courses.forEach(course -> {
                        List<CourseSession> sessions = course.getSessions();

                        sessions.forEach(session ->
                                SessionScheduler
                                        .getInstance(MyCoursesActivity.this).schedule(session));
                    });

                    break;

                case ERROR:
                    final String msg = "Không thể lên lịch báo thức giờ học";
                    Toast.makeText(MyCoursesActivity.this, msg, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void setupTodoReminders() {
        TodoRepository.getInstance().getAllTodos().observe(MyCoursesActivity.this, stateModel -> {
            switch (stateModel.getStatus()) {
                case SUCCESS:
                    stateModel.getData().forEach(todo -> {
                        long deadline = todo.getDeadline() * 1000;

                        if (deadline > System.currentTimeMillis()) {
                            TodoScheduler.getInstance(MyCoursesActivity.this).schedule(todo);
                        }
                    });

                    break;

                case ERROR:
                    final String msg = "Không thể lên lịch báo thức công việc";
                    Toast.makeText(MyCoursesActivity.this, msg, Toast.LENGTH_SHORT).show();
                    break;
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
        NavDestination destination = mNavController.getCurrentDestination();

        if (destination != null && destination.getId() == R.id.navigation_courses) {
            super.onBackPressed();
        } else {
            mNavController.navigateUp();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkListener, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mNetworkListener);
    }
}
