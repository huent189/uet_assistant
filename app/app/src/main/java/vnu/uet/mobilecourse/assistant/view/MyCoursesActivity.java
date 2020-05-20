package vnu.uet.mobilecourse.assistant.view;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import vnu.uet.mobilecourse.assistant.R;

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
                case R.id.navigation_chat:
                    hideBottomNavigator();
                    break;

                default:
                    showBottomNavigator();
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
