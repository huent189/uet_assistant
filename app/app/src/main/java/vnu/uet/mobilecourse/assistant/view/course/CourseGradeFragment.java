package vnu.uet.mobilecourse.assistant.view.course;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.Locale;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.GradeAdapter;
import vnu.uet.mobilecourse.assistant.model.Course;
import vnu.uet.mobilecourse.assistant.model.ICourse;
import vnu.uet.mobilecourse.assistant.viewmodel.CourseGradeViewModel;

public class CourseGradeFragment extends Fragment {

    private CourseGradeViewModel mViewModel;
    private GradeAdapter mGradeAdapter;
    private int mCourseId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_course_grade, container, false);

        mViewModel = new ViewModelProvider(this).get(CourseGradeViewModel.class);

        // get bundle from prev fragment
        Bundle args = getArguments();
        assert args != null;
        ICourse course = args.getParcelable("course");

        if (course instanceof Course) {
            // get course id from bundle
            mCourseId = ((Course) course).getId();

            initializeGradeListView(root);
            initializeSummaryView(root);

        } else {
            LinearLayout layoutContainer = root.findViewById(R.id.layoutContainer);
            layoutContainer.setVisibility(View.GONE);

            TextView tvEnrollError = root.findViewById(R.id.tvEnrollError);
            tvEnrollError.setVisibility(View.VISIBLE);
        }

        return root;
    }

    private void initializeSummaryView(View root) {
        TextView tvGradeProgress = root.findViewById(R.id.tvGradeProgress);

        // shimmer layout for loading state
        ShimmerFrameLayout shimmerTvGradeProgress = root.findViewById(R.id.shimmerTvGradeProgress);
        shimmerTvGradeProgress.startShimmerAnimation();

        mViewModel.getCourseSummaryGrade(mCourseId).observe(getViewLifecycleOwner(), grade -> {
            if (grade == null) {
                // cant find summary grade -> loading state
                shimmerTvGradeProgress.setVisibility(View.VISIBLE);
                tvGradeProgress.setVisibility(View.INVISIBLE);

            } else {
                // summary grade found
                // success state
                tvGradeProgress.setVisibility(View.VISIBLE);
                shimmerTvGradeProgress.setVisibility(View.INVISIBLE);

                double maxGrade = grade.getMaxGrade();
                double userGrade = grade.getUserGrade();

                String gradeProgress;

                // course has graded
                if (userGrade >= 0) {
                    gradeProgress = String.format(Locale.ROOT, "%.0f/%.0f", userGrade, maxGrade);
                }
                // course hasn't graded yet
                else {
                    gradeProgress = String.format(Locale.ROOT, "/%.0f", maxGrade);
                }

                tvGradeProgress.setText(gradeProgress);
            }
        });
    }

    private void initializeGradeListView(View root) {
        // recycle view for visualize grade of assignment/quiz
        RecyclerView rvGrades = root.findViewById(R.id.rvGrades);
        rvGrades.setLayoutManager(new LinearLayoutManager(this.getContext()));

        // shimmer layout for loading state
        ShimmerFrameLayout shimmerRvGrades = root.findViewById(R.id.shimmerRvGrades);
        shimmerRvGrades.startShimmerAnimation();

        mViewModel.getCourseGrades(mCourseId).observe(getViewLifecycleOwner(), grades -> {
            // grades not found -> loading state
            if (grades == null) {
                shimmerRvGrades.setVisibility(View.VISIBLE);
                rvGrades.setVisibility(View.INVISIBLE);

            }
            // success state, update recycle view
            else {
                shimmerRvGrades.setVisibility(View.INVISIBLE);
                rvGrades.setVisibility(View.VISIBLE);

                mGradeAdapter = new GradeAdapter(grades, CourseGradeFragment.this);
                rvGrades.setAdapter(mGradeAdapter);
            }
        });
    }
}
