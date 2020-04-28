package vnu.uet.mobilecourse.assistant.view;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import vnu.uet.mobilecourse.assistant.R;

public class MyCoursesActivity extends AppCompatActivity {

    private NavController navController;

    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_courses);
        navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_courses, R.id.navigation_explore_course, R.id.navigation_chat, R.id.navigation_calendar)
//                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                switch (destination.getId()) {
                    case R.id.navigation_chat:
                        hideBottomNavigator();
                        break;

                    default:
                        showBottomNavigator();
                        break;
                }
            }
        });
    }

    private void hideBottomNavigator() {
        navView.setVisibility(View.GONE);
    }

    private void showBottomNavigator() {
        navView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        navController.navigateUp();
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
