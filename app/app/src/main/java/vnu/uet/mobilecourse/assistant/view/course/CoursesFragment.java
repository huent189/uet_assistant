package vnu.uet.mobilecourse.assistant.view.course;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.AllCoursesAdapter;
import vnu.uet.mobilecourse.assistant.adapter.RecentlyCoursesAdapter;
import vnu.uet.mobilecourse.assistant.model.Course;
import vnu.uet.mobilecourse.assistant.viewmodel.CoursesViewModel;

public class CoursesFragment extends Fragment {

    private CoursesViewModel viewModel;

    private NavController navController;

    private int prevTopAllItemPosition, prevTopRecentlyItemPosition;

    private LinearLayoutManager allLayoutManager, recentlyLayoutManager;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        CoursesFragment currentFragment = this;

        View root = inflater.inflate(R.layout.fragment_courses, container, false);

        // restore expandable toolbar state
        CoordinatorLayout coordinatorLayout = root.findViewById(R.id.coordinator_layout);
        ViewCompat.requestApplyInsets(coordinatorLayout);

        viewModel = new ViewModelProvider(currentFragment).get(CoursesViewModel.class);
        viewModel.setView(currentFragment);
        viewModel.initialize();

        ShimmerFrameLayout shimmerRvCourseRecently = root.findViewById(R.id.shimmerRvCourseRecently);
        shimmerRvCourseRecently.startShimmerAnimation();

        ShimmerFrameLayout shimmerRvAllCourses = root.findViewById(R.id.shimmerRvAllCourses);
        shimmerRvAllCourses.startShimmerAnimation();

        RecyclerView rvCourseRecently = initializeRecentlyCoursesView(root);

        RecyclerView rvAllCourses = initializeAllCoursesView(root);

        viewModel.getRecentlyCourses().observe(getViewLifecycleOwner(), new Observer<List<Course>>() {
            @Override
            public void onChanged(List<Course> courses) {
                if (courses == null) {
                    shimmerRvCourseRecently.startShimmerAnimation();
                    rvCourseRecently.setVisibility(View.INVISIBLE);

                } else {
                    if (rvCourseRecently.getVisibility() == View.INVISIBLE) {
                        shimmerRvCourseRecently.setVisibility(View.INVISIBLE);
                        rvCourseRecently.setVisibility(View.VISIBLE);
                    }

                    RecentlyCoursesAdapter recentlyCoursesAdapter = new RecentlyCoursesAdapter(courses, currentFragment);
                    rvCourseRecently.setAdapter(recentlyCoursesAdapter);

                    // restore scroll position
                    recentlyLayoutManager.scrollToPosition(prevTopRecentlyItemPosition);
                }
            }
        });

        viewModel.getCourses().observe(getViewLifecycleOwner(), new Observer<List<Course>>() {
            @Override
            public void onChanged(List<Course> courses) {
                if (courses == null) {
                    shimmerRvAllCourses.startShimmerAnimation();
                    rvAllCourses.setVisibility(View.INVISIBLE);

                } else {
                    if (rvAllCourses.getVisibility() == View.INVISIBLE) {
                        shimmerRvAllCourses.setVisibility(View.INVISIBLE);
                        rvAllCourses.setVisibility(View.VISIBLE);
                    }

                    AllCoursesAdapter allCoursesAdapter = new AllCoursesAdapter(courses, currentFragment);
                    rvAllCourses.setAdapter(allCoursesAdapter);

                    // restore scroll position
                    allLayoutManager.scrollToPosition(prevTopAllItemPosition);
                }
            }
        });

        Toolbar toolbar = initializeToolbar(root);

        return root;
    }

    private Toolbar initializeToolbar(View root) {
        Toolbar toolbar = null;

        AppCompatActivity activity = (AppCompatActivity) getActivity();

        if (activity != null) {
            toolbar = root.findViewById(R.id.toolbar);

            activity.setSupportActionBar(toolbar);

            setHasOptionsMenu(true);
        }

        return toolbar;
    }

    private RecyclerView initializeRecentlyCoursesView(View root) {
        RecyclerView rvCourseRecently = root.findViewById(R.id.rvCourseRecently);

        recentlyLayoutManager = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false);

        rvCourseRecently.setLayoutManager(recentlyLayoutManager);

        return rvCourseRecently;
    }

    private RecyclerView initializeAllCoursesView(View root) {
        RecyclerView rvAllCourses = root.findViewById(R.id.rvAllCourses);

        allLayoutManager = new LinearLayoutManager(this.getContext());

        rvAllCourses.setLayoutManager(allLayoutManager);

        return rvAllCourses;
    }

    @Override
    public void onPause() {
        super.onPause();

        prevTopRecentlyItemPosition = recentlyLayoutManager.findFirstCompletelyVisibleItemPosition();
        prevTopAllItemPosition = allLayoutManager.findFirstCompletelyVisibleItemPosition();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();

        inflater.inflate(R.menu.course_toolbar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}
