package vnu.uet.mobilecourse.assistant.view.course;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.shimmer.ShimmerFrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.CourseContentAdapter;
import vnu.uet.mobilecourse.assistant.viewmodel.CourseProgressViewModel;

public class CourseProgressFragment extends Fragment {

    private CourseProgressViewModel mViewModel;
    private int mPrevTopItemPosition;
    private LinearLayoutManager mLayoutManager;
    private CourseContentAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(CourseProgressViewModel.class);

        View root = inflater.inflate(R.layout.fragment_course_progress, container, false);

        // find rvMaterials
        RecyclerView rvMaterials = root.findViewById(R.id.rvMaterials);
        mLayoutManager = new LinearLayoutManager(getContext());
        rvMaterials.setLayoutManager(mLayoutManager);

        // find shimmer layout & start shimmer animation
        ShimmerFrameLayout shimmerRvTasks = root.findViewById(R.id.shimmerRvTasks);
        shimmerRvTasks.startShimmerAnimation();

        // get bundle from prev fragment
        Bundle args = getArguments();

        if (args != null) {
            // get course id from bundle
            int courseId = args.getInt("courseId");

            Fragment thisFragment = this;

            mViewModel.getContent(courseId).observe(getViewLifecycleOwner(), contents -> {
                // contents haven't loaded yet
                // then show shimmer layout & hide rvMaterials
                if (contents == null || contents.isEmpty()) {
                    rvMaterials.setVisibility(View.INVISIBLE);
                    shimmerRvTasks.setVisibility(View.VISIBLE);
                }

                // contents loaded completely
                // then hide shimmer layout
                // setup recycle view adapter & show rvMaterials
                else {
                    rvMaterials.setVisibility(View.VISIBLE);
                    shimmerRvTasks.setVisibility(View.GONE);

                    // update new adapter with newest data
                    mAdapter = new CourseContentAdapter(contents, thisFragment);
                    rvMaterials.setAdapter(mAdapter);

                    // restore expandable state
                    mAdapter.onRestoreInstanceState(args);

                    // restore scroll position
                    mLayoutManager.scrollToPosition(mPrevTopItemPosition);
                }
            });
        }

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();

        // save expandable state
        Bundle savedInstanceState = getArguments();
        if (savedInstanceState != null)
            mAdapter.onSaveInstanceState(savedInstanceState);

        mPrevTopItemPosition = mLayoutManager.findFirstCompletelyVisibleItemPosition();
    }


}
