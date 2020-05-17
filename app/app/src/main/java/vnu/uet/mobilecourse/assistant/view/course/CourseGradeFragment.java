package vnu.uet.mobilecourse.assistant.view.course;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.GradeAdapter;
import vnu.uet.mobilecourse.assistant.model.Grade;
import vnu.uet.mobilecourse.assistant.viewmodel.CourseGradeViewModel;

public class CourseGradeFragment extends Fragment {

    private CourseGradeViewModel viewModel;

    private GradeAdapter gradeAdapter;

    private int courseId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_course_grade, container, false);

        viewModel = new ViewModelProvider(this).get(CourseGradeViewModel.class);

        initializeGradeListView(root);

        TextView tvGradeProgress = root.findViewById(R.id.tvGradeProgress);

        ShimmerFrameLayout shimmerTvGradeProgress = root.findViewById(R.id.shimmerTvGradeProgress);
        shimmerTvGradeProgress.startShimmerAnimation();

        viewModel.getCourseSummaryGrade(courseId).observe(getViewLifecycleOwner(), new Observer<Grade>() {
            @Override
            public void onChanged(Grade grade) {
                if (grade == null) {
                    shimmerTvGradeProgress.setVisibility(View.VISIBLE);
                    tvGradeProgress.setVisibility(View.INVISIBLE);

                } else {
                    tvGradeProgress.setVisibility(View.VISIBLE);
                    shimmerTvGradeProgress.setVisibility(View.INVISIBLE);

                    String gradeProgress = String.format(Locale.ROOT,
                            "%.0f/%.0f",
                            grade.getUserGrade(),
                            grade.getMaxGrade());

                    tvGradeProgress.setText(gradeProgress);
                }
            }
        });

        return root;
    }

    private void initializeGradeListView(View root) {
        RecyclerView rvGrades = root.findViewById(R.id.rvGrades);
        rvGrades.setLayoutManager(new LinearLayoutManager(this.getContext()));

        // get bundle from prev fragment
        Bundle args = getArguments();

        if (args != null) {
            // get course id from bundle
            courseId = args.getInt("courseId");

            Fragment thisFragment = this;

            ShimmerFrameLayout shimmerRvGrades = root.findViewById(R.id.shimmerRvGrades);
            shimmerRvGrades.startShimmerAnimation();

            viewModel.getCourseGrades(courseId).observe(getViewLifecycleOwner(), new Observer<List<Grade>>() {
                @Override
                public void onChanged(List<Grade> grades) {
                    if (grades == null || grades.isEmpty()) {
                        shimmerRvGrades.setVisibility(View.VISIBLE);
                        rvGrades.setVisibility(View.INVISIBLE);

                    } else {
                        shimmerRvGrades.setVisibility(View.INVISIBLE);
                        rvGrades.setVisibility(View.VISIBLE);

                        gradeAdapter = new GradeAdapter(grades, thisFragment);
                        rvGrades.setAdapter(gradeAdapter);
                    }
                }
            });
        }
    }
}
