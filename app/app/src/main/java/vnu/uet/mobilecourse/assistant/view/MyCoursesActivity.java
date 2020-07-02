package vnu.uet.mobilecourse.assistant.view;

import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.alarm.scheduler.SessionScheduler;
import vnu.uet.mobilecourse.assistant.alarm.scheduler.TodoScheduler;
import vnu.uet.mobilecourse.assistant.database.DAO.CourseInfoDAO;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseInfo;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseSession;
import vnu.uet.mobilecourse.assistant.repository.firebase.FirebaseUserRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.NavigationBadgeRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.TodoRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.chatnoti.MyFirebaseMessagingService;
import vnu.uet.mobilecourse.assistant.util.NetworkChangeReceiver;
import vnu.uet.mobilecourse.assistant.util.NetworkUtils;
import vnu.uet.mobilecourse.assistant.util.NotificationHelper;
import vnu.uet.mobilecourse.assistant.view.chat.ChatFragment;
import vnu.uet.mobilecourse.assistant.view.notification.NotificationsFragment;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;
import vnu.uet.mobilecourse.assistant.work.courses.CourseDataSynchronization;

import java.util.List;

public class MyCoursesActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private NavController mNavController;

    private BottomNavigationView mNavView;

    private View mNotificationBadge, mChatBadge;

    private NavigationBadgeRepository mNavigationBadgeRepo = NavigationBadgeRepository.getInstance();

    private NetworkChangeReceiver mNetworkListener = NetworkChangeReceiver.getInstance();

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_courses);

        mNavView = findViewById(R.id.nav_view);

        setupFCMToken();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(mNavView, mNavController);

        setupNavigationBadges();


        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkPermission())
            {
                // Code for above or equal 23 API Oriented Device
                // Your Permission granted already .Do next code
            } else {
                requestPermission(); // Code for permission
            }
        }
        else
        {

            // Code for Below 23 API Oriented Device
            // Do next code
        }

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

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            Toast.makeText(this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
//        } else {
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
//        }
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    private void checkIfOpenByNotification() {
        if (NotificationHelper.ACTION_OPEN.equals(getIntent().getAction())) {

            String menuFragment = getIntent().getStringExtra("fragment");

            if (menuFragment != null) {
                if (menuFragment.equals(NotificationsFragment.class.getName())) {
                    mNavController.navigate(R.id.action_navigation_courses_to_navigation_notifications);
                } else if (menuFragment.equals(ChatFragment.class.getName())) {
                    mNavController.navigate(R.id.action_navigation_courses_to_navigation_chat);
                }
            }
        }
    }

    private void setupFCMToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String token = instanceIdResult.getToken();
                        Log.i("FCM Token", token);
                        MyFirebaseMessagingService service = new MyFirebaseMessagingService();
                        service.updateToken(token);
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
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

        FirebaseUserRepository.getInstance().changeOnlineState(true);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mNetworkListener);

        FirebaseUserRepository.getInstance().changeOnlineState(false);
    }
}
