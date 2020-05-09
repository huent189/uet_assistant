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

    private RecentlyCoursesAdapter recentlyCoursesAdapter;

    private CoursesViewModel viewModel;

    private NavController navController;

    private Toolbar toolbar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_courses, container, false);

        viewModel = new ViewModelProvider(this).get(CoursesViewModel.class);

        viewModel.initialize();

        ShimmerRecyclerView shimmerRvCourseRecently = root.findViewById(R.id.shimmerRvCourseRecently);
        shimmerRvCourseRecently.showShimmerAdapter();


        CoursesFragment fragment = this;


//        ViewPager recentlyCoursesView = root.findViewById(R.id.vpCourseRecently);
//        recentlyCoursesView.setAdapter(recentlyCoursesAdapter);

        RecyclerView rvCourseRecently = root.findViewById(R.id.rvCourseRecently);
        rvCourseRecently.setAdapter(recentlyCoursesAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvCourseRecently.setLayoutManager(layoutManager);

        viewModel.getCourses().observe(getViewLifecycleOwner(), new Observer<List<Course>>() {
            @Override
            public void onChanged(List<Course> courses) {
                if (courses == null) {
                    shimmerRvCourseRecently.showShimmerAdapter();
                    rvCourseRecently.setVisibility(View.INVISIBLE);
                    recentlyCoursesAdapter = new RecentlyCoursesAdapter(new ArrayList<Course>(), fragment);
                } else {
                    shimmerRvCourseRecently.hideShimmerAdapter();
                    rvCourseRecently.setVisibility(View.VISIBLE);
                    recentlyCoursesAdapter = new RecentlyCoursesAdapter(courses, fragment);
                    rvCourseRecently.setAdapter(recentlyCoursesAdapter);
                    recentlyCoursesAdapter.notifyDataSetChanged();
                }

            }
        });

        initializeToolbar(root);

//        initializeRecentlyCoursesView(root);

//        initializeAllCoursesView(root);

        return root;
    }

    private void initializeToolbar(View root) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        if (activity != null) {
            toolbar = root.findViewById(R.id.toolbar);

            activity.setSupportActionBar(toolbar);

            setHasOptionsMenu(true);
        }
    }

    private void initializeRecentlyCoursesView(View root) {
        recentlyCoursesAdapter = new RecentlyCoursesAdapter(viewModel.getCourses().getValue(), this);

//        ViewPager recentlyCoursesView = root.findViewById(R.id.vpCourseRecently);
//        recentlyCoursesView.setAdapter(recentlyCoursesAdapter);

        RecyclerView rvCourseRecently = root.findViewById(R.id.rvCourseRecently);
        rvCourseRecently.setAdapter(recentlyCoursesAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvCourseRecently.setLayoutManager(layoutManager);

    }

    private void initializeAllCoursesView(View root) {
        List<Course> courses = viewModel.getCourses().getValue();

        AllCoursesAdapter allCoursesAdapter = new AllCoursesAdapter(courses, this);

        RecyclerView allCoursesView = root.findViewById(R.id.rvAllCourses);

        allCoursesView.setAdapter(allCoursesAdapter);
        allCoursesView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();

        inflater.inflate(R.menu.course_toolbar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchItem.getActionView();
//        LinearLayout searchEditFrame = searchView.findViewById(R.id.search_edit_frame); // Get the Linear Layout
//        // Get the associated LayoutParams and set leftMargin
//        float scale = getResources().getDisplayMetrics().density;
//        int dpAsPixels = (int) (16*scale + 0.5f);
//        searchView.setPadding(0,0,0,0);

//        Drawable background = ContextCompat.getDrawable(getActivity(), R.drawable.edit_text_background);
//        searchView.setBackground(background);

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
