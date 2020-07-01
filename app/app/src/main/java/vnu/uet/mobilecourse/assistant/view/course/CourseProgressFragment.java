package vnu.uet.mobilecourse.assistant.view.course;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.facebook.shimmer.ShimmerFrameLayout;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.CourseContentAdapter;
import vnu.uet.mobilecourse.assistant.model.Course;
import vnu.uet.mobilecourse.assistant.model.ICourse;
import vnu.uet.mobilecourse.assistant.viewmodel.CourseProgressViewModel;

import java.util.stream.Collectors;

import static vnu.uet.mobilecourse.assistant.model.material.CourseConstant.MaterialType.GENERAL;

public class CourseProgressFragment extends Fragment {

    private CourseProgressViewModel mViewModel;

    private LinearLayoutManager mLayoutManager;
    private CourseContentAdapter mAdapter;

    private RecyclerView mRvMaterials;

    private Bundle mRecyclerViewState;

//    private int mPrevTopItemPosition;

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
        mRvMaterials = root.findViewById(R.id.rvMaterials);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRvMaterials.setLayoutManager(mLayoutManager);

        // find shimmer layout & start shimmer animation
        ShimmerFrameLayout shimmerRvTasks = root.findViewById(R.id.shimmerRvTasks);
        shimmerRvTasks.startShimmerAnimation();

        // get bundle from prev fragment
        Bundle args = getArguments();
        assert args != null;
        ICourse course = args.getParcelable("course");

        if (course instanceof Course) {
            // get course id from bundle
            int courseId = ((Course) course).getId();

            mViewModel.getContent(courseId).observe(getViewLifecycleOwner(), contents -> {
                // contents haven't loaded yet
                // then show shimmer layout & hide rvMaterials
                if (contents == null) {
                    mRvMaterials.setVisibility(View.INVISIBLE);
                    shimmerRvTasks.setVisibility(View.VISIBLE);
                }

                // contents loaded completely
                // then hide shimmer layout
                // setup recycle view adapter & show rvMaterials
                else {
                    mRvMaterials.setVisibility(View.VISIBLE);
                    shimmerRvTasks.setVisibility(View.GONE);

                    // update new adapter with newest data
                    contents = contents.stream()
                            .filter(item -> !item.getWeekInfo().getTitle().equals(GENERAL))
                            .collect(Collectors.toList());
                    mAdapter = new CourseContentAdapter(contents, CourseProgressFragment.this);
                    mRvMaterials.setAdapter(mAdapter);



                    restoreRecycleViewState();

                    // restore scroll position
                }
            });
        } else {
            TextView tvEnrollError = root.findViewById(R.id.tvEnrollError);
            tvEnrollError.setVisibility(View.VISIBLE);
            mRvMaterials.setVisibility(View.GONE);
            shimmerRvTasks.setVisibility(View.GONE);
        }

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();

        // save expandable state
        saveRecycleViewState();
    }

    private static final String KEY_RECYCLER_STATE = CourseContentAdapter.class.getName();

    public void saveRecycleViewState() {
        mRecyclerViewState = new Bundle();

        Parcelable onSaveInstanceState = mLayoutManager.onSaveInstanceState();
        mRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, onSaveInstanceState);
//        mAdapter.onSaveInstanceState(mRecyclerViewState);

        if (mRecyclerViewState != null && mAdapter != null) {
            mAdapter.onSaveInstanceState(mRecyclerViewState);
        }

//        mPrevTopItemPosition = mLayoutManager.findFirstCompletelyVisibleItemPosition();
    }

    public void restoreRecycleViewState() {
        if (mRecyclerViewState != null) {
            // restore expandable state
            mAdapter.onRestoreInstanceState(mRecyclerViewState);

            mAdapter.onRestoreInstanceState(mRecyclerViewState);
            Parcelable onSaveInstanceState = mRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            mLayoutManager.onRestoreInstanceState(onSaveInstanceState);

//            mLayoutManager.scrollToPosition(mPrevTopItemPosition);
        }
    }

}
