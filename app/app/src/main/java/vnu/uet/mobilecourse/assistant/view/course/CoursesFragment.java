package vnu.uet.mobilecourse.assistant.view.course;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.AllCoursesAdapter;
import vnu.uet.mobilecourse.assistant.adapter.RecentlyCoursesAdapter;
import vnu.uet.mobilecourse.assistant.viewmodel.CoursesViewModel;

public class CoursesFragment extends Fragment {

    private CoursesViewModel mViewModel;

    private NavController mNavController;

    private int mPrevCoursePositionForAll, mPrevCoursePositionForRecently;

    private AllCoursesAdapter mCoursesAdapter;

    private LinearLayoutManager mAllLayoutManager, mRecentlyLayoutManager;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_courses, container, false);

        // restore expandable toolbar state
        CoordinatorLayout coordinatorLayout = root.findViewById(R.id.coordinator_layout);
        ViewCompat.requestApplyInsets(coordinatorLayout);

        mViewModel = new ViewModelProvider(this).get(CoursesViewModel.class);

        ShimmerFrameLayout shimmerRvCourseRecently = root.findViewById(R.id.shimmerRvCourseRecently);
        shimmerRvCourseRecently.startShimmerAnimation();

        ShimmerFrameLayout shimmerRvAllCourses = root.findViewById(R.id.shimmerRvAllCourses);
        shimmerRvAllCourses.startShimmerAnimation();

        RecyclerView rvCourseRecently = initializeRecentlyCoursesView(root);

        RecyclerView rvAllCourses = initializeAllCoursesView(root);

        mViewModel.getRecentlyCourses().observe(getViewLifecycleOwner(), courses -> {
            if (courses == null) {
                shimmerRvCourseRecently.startShimmerAnimation();
                rvCourseRecently.setVisibility(View.INVISIBLE);

            } else {
                if (rvCourseRecently.getVisibility() == View.INVISIBLE) {
                    shimmerRvCourseRecently.setVisibility(View.INVISIBLE);
                    rvCourseRecently.setVisibility(View.VISIBLE);
                }

                RecentlyCoursesAdapter adapter = new RecentlyCoursesAdapter(courses, CoursesFragment.this);
                rvCourseRecently.setAdapter(adapter);

                // restore scroll position
                mRecentlyLayoutManager.scrollToPosition(mPrevCoursePositionForRecently);
            }
        });

        mViewModel.getCourses().observe(getViewLifecycleOwner(), stateModel -> {
            switch (stateModel.getStatus()) {
                case LOADING:
                    shimmerRvAllCourses.startShimmerAnimation();
                    rvAllCourses.setVisibility(View.INVISIBLE);
                    break;


                case SUCCESS:
                    if (rvAllCourses.getVisibility() == View.INVISIBLE) {
                        shimmerRvAllCourses.setVisibility(View.INVISIBLE);
                        rvAllCourses.setVisibility(View.VISIBLE);
                    }

                    mCoursesAdapter = new AllCoursesAdapter(stateModel.getData(), CoursesFragment.this);
                    rvAllCourses.setAdapter(mCoursesAdapter);

                    // restore scroll position
                    mAllLayoutManager.scrollToPosition(mPrevCoursePositionForAll);

                    break;
            }


//            if (courses == null) {
//                shimmerRvAllCourses.startShimmerAnimation();
//                rvAllCourses.setVisibility(View.INVISIBLE);
//
//            } else {
//                if (rvAllCourses.getVisibility() == View.INVISIBLE) {
//                    shimmerRvAllCourses.setVisibility(View.INVISIBLE);
//                    rvAllCourses.setVisibility(View.VISIBLE);
//                }
//
//                AllCoursesAdapter adapter = new AllCoursesAdapter(courses, CoursesFragment.this);
//                rvAllCourses.setAdapter(adapter);
//
//                // restore scroll position
//                mAllLayoutManager.scrollToPosition(mPrevCoursePositionForAll);
//            }
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

        mRecentlyLayoutManager = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false);

        rvCourseRecently.setLayoutManager(mRecentlyLayoutManager);

        return rvCourseRecently;
    }

    private RecyclerView initializeAllCoursesView(View root) {
        RecyclerView rvAllCourses = root.findViewById(R.id.rvAllCourses);

        mAllLayoutManager = new LinearLayoutManager(this.getContext());

        rvAllCourses.setLayoutManager(mAllLayoutManager);

        return rvAllCourses;
    }

    @Override
    public void onPause() {
        super.onPause();

        mPrevCoursePositionForRecently = mRecentlyLayoutManager
                .findFirstCompletelyVisibleItemPosition();

        mPrevCoursePositionForAll = mAllLayoutManager
                .findFirstCompletelyVisibleItemPosition();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();

        inflater.inflate(R.menu.course_toolbar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

//        View searchViewContainer = searchItem.getActionView();
        SearchView searchView = (SearchView) searchItem.getActionView();
//        SearchView searchView = searchViewContainer.findViewById(R.id.searchView);

//        ViewGroup.LayoutParams params = searchView.getLayoutParams();
//        params.height = 42;
//        searchView.requestLayout();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mCoursesAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}
