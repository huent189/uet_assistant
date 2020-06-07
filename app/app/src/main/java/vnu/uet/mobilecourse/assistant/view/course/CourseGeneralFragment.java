package vnu.uet.mobilecourse.assistant.view.course;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.CourseGeneralMaterialAdapter;
import vnu.uet.mobilecourse.assistant.adapter.CourseSessionAdapter;
import vnu.uet.mobilecourse.assistant.model.Course;
import vnu.uet.mobilecourse.assistant.model.ICourse;
import vnu.uet.mobilecourse.assistant.model.Material;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseInfo;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseSession;
import vnu.uet.mobilecourse.assistant.util.CONST;
import vnu.uet.mobilecourse.assistant.viewmodel.CourseGeneralViewModel;

import static vnu.uet.mobilecourse.assistant.model.material.CourseConstant.MaterialType.GENERAL;

public class CourseGeneralFragment extends Fragment {

    private CourseGeneralViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(CourseGeneralViewModel.class);

        View root = inflater.inflate(R.layout.fragment_course_general, container, false);

        Bundle args = getArguments();
        if (args != null) {
            String courseCode = args.getString("courseCode");
            assert courseCode != null;
            courseCode = courseCode.replace(CONST.COURSE_PREFIX + CONST.UNDERSCORE, "")
                    .replace(CONST.UNDERSCORE, CONST.SPACE);

            TextView tvCourseTitle = root.findViewById(R.id.tvCourseTitle);
            TextView tvCourseId = root.findViewById(R.id.tvCourseId);
            TextView tvCredits = root.findViewById(R.id.tvCredits);

            CircularProgressBar cpbProgress = root.findViewById(R.id.cpbProgress);

            TextView tvProgress = root.findViewById(R.id.tvProgress);

            ICourse course = args.getParcelable("course");

            if (course instanceof Course) {
                float progress = (float) ((Course) course).getProgress();
                tvProgress.setText(String.format(Locale.ROOT, "%.0f%%", progress));
                cpbProgress.setProgressWithAnimation(progress);
            }

            assert course != null;
            tvCourseId.setText(course.getCode());
            tvCourseTitle.setText(course.getTitle());

            int courseId = args.getInt("courseId");
            initializeGeneralMaterialsView(root, courseId);

            RecyclerView rvSessions = initializeSessionsView(root);
            mViewModel.getCourseInfo(courseCode).observe(getViewLifecycleOwner(), stateModel -> {
                switch (stateModel.getStatus()) {
                    case LOADING:
                        tvCredits.setText(R.string.title_loading);
                        break;

                    case ERROR:
                        tvCredits.setText(R.string.title_error);
                        break;

                    case SUCCESS:
                        CourseInfo courseInfo = stateModel.getData();

                        tvCredits.setText(String.valueOf(courseInfo.getCredits()));

                        List<CourseSession> sessions = courseInfo.getSessions();
                        CourseSessionAdapter adapter = new CourseSessionAdapter(sessions, CourseGeneralFragment.this);
                        rvSessions.setAdapter(adapter);

                        break;
                }
            });

            initializeParticipantsView(root, courseCode);
        }

        return root;
    }

    private void initializeGeneralMaterialsView(View root, int courseId) {
        RecyclerView rvGeneralMaterials = root.findViewById(R.id.rvGeneralMaterials);
        rvGeneralMaterials.setLayoutManager(new LinearLayoutManager(getContext()));

        TextView tvGeneralMaterials = root.findViewById(R.id.tvGeneralMaterials);

        mViewModel.getContent(courseId).observe(getViewLifecycleOwner(), courseOverviews -> {
            if (courseOverviews != null && !courseOverviews.isEmpty()) {
                courseOverviews.stream()
                        .filter(item -> item.getWeekInfo().getTitle().equals(GENERAL))
                        .findFirst()
                        .ifPresent(courseOverview -> {
                            List<Material> materials = courseOverview.getMaterials();

                            if (materials.isEmpty()) {
                                tvGeneralMaterials.setVisibility(View.GONE);

                            } else {
                                tvGeneralMaterials.setVisibility(View.VISIBLE);

                                CourseGeneralMaterialAdapter adapter =
                                        new CourseGeneralMaterialAdapter(materials, CourseGeneralFragment.this);

                                rvGeneralMaterials.setAdapter(adapter);
                            }
                        });
            } else {
                tvGeneralMaterials.setVisibility(View.GONE);
            }
        });
    }

    private void initializeParticipantsView(View root, String courseCode) {
        TextView tvStudents = root.findViewById(R.id.tvStudents);

        if (courseCode.isEmpty()) {
            tvStudents.setText(R.string.title_error);

        } else {
            mViewModel.getParticipants(courseCode).observe(getViewLifecycleOwner(), stateModel -> {
                switch (stateModel.getStatus()) {
                    case LOADING:
                        tvStudents.setText(R.string.title_loading);
                        break;

                    case ERROR:
                        tvStudents.setText(R.string.title_error);
                        break;

                    case SUCCESS:
                        int participants = stateModel.getData().size();
                        tvStudents.setText(String.valueOf(participants));
                        break;
                }
            });
        }
    }

    private RecyclerView initializeSessionsView(View root) {
        RecyclerView rvSessions = root.findViewById(R.id.rvSessions);

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false);

        rvSessions.setLayoutManager(layoutManager);

        return rvSessions;
    }
}
