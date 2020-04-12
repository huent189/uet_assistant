package vnu.uet.mobilecourse.assistant.ui.view;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import vnu.uet.mobilecourse.assistant.R;

public class MyCoursesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_courses);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_courses, R.id.navigation_explore_course, R.id.navigation_chat, R.id.navigation_calendar)
//                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

//        final FragmentActivity activity = this;
//        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
////                FragmentManager fragmentManager = getSupportFragmentManager();
////
////                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
////
////                Fragment fragment = new NotificationsFragment();
////
////                // open explore course fragment
////                fragmentManager.beginTransaction()
////                        // insert the fragment by replacing an existing fragment
////                        .replace(R.id.nav_host_fragment, fragment)
////                        // add previous fragment into back stack
////                        // commit transaction
////                        .commit();
//
//                DashboardFragmentManager fragmentManager = new DashboardFragmentManager(activity);
//
//                switch (item.getItemId()) {
//                    case R.id.navigation_courses:
//                        fragmentManager.change(CoursesFragment.class);
//                        break;
//                    case R.id.navigation_notifications:
//                        fragmentManager.change(NotificationsFragment.class);
//                        break;
//                    case R.id.navigation_chat:
//                        fragmentManager.change(ChatFragment.class);
//                        break;
//                    case R.id.navigation_calendar:
//                        fragmentManager.change(CalendarFragment.class);
//                        break;
//                }
//
//                return true;
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        int count = fragmentManager.getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();

        } else {
            fragmentManager.popBackStack();
        }
    }
}
