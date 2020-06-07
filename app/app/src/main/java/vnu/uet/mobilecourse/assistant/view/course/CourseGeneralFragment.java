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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.CourseSessionAdapter;
import vnu.uet.mobilecourse.assistant.model.Course;
import vnu.uet.mobilecourse.assistant.model.ICourse;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseInfo;
import vnu.uet.mobilecourse.assistant.model.firebase.Participant_CourseSubCol;
import vnu.uet.mobilecourse.assistant.util.CONST;
import vnu.uet.mobilecourse.assistant.viewmodel.CourseGeneralViewModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class CourseGeneralFragment extends Fragment {

    private CourseGeneralViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(CourseGeneralViewModel.class);

        View root = inflater.inflate(R.layout.fragment_course_general, container, false);

        RecyclerView rvSessions = initializeSessionsView(root);

        Bundle args = getArguments();
        if (args != null) {
            String id = args.getString("courseCode");
            assert id != null;
            id = id.replace(CONST.COURSE_PREFIX + CONST.UNDERSCORE, "")
                    .replace(CONST.UNDERSCORE, CONST.SPACE);

            TextView tvCourseTitle = root.findViewById(R.id.tvCourseTitle);
            TextView tvCourseId = root.findViewById(R.id.tvCourseId);
            TextView tvCredits = root.findViewById(R.id.tvCredits);
            TextView tvStudents = root.findViewById(R.id.tvStudents);

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

            mViewModel.getCourseInfo(id).observe(getViewLifecycleOwner(), new Observer<StateModel<CourseInfo>>() {
                @Override
                public void onChanged(StateModel<CourseInfo> stateModel) {
                    switch (stateModel.getStatus()) {
                        case SUCCESS:
                            CourseInfo courseInfo = stateModel.getData();

                            tvCredits.setText(String.valueOf(courseInfo.getCredits()));

                            CourseSessionAdapter adapter = new CourseSessionAdapter(courseInfo.getSessions(), CourseGeneralFragment.this);
                            rvSessions.setAdapter(adapter);

                            break;
                    }
                }
            });

            if (id.isEmpty()) {
                tvStudents.setText("???");

            } else {
                mViewModel.getParticipants(id).observe(getViewLifecycleOwner(), new Observer<StateModel<List<Participant_CourseSubCol>>>() {
                    @Override
                    public void onChanged(StateModel<List<Participant_CourseSubCol>> stateModel) {
                        switch (stateModel.getStatus()) {
                            case SUCCESS:
                                int participants = stateModel.getData().size();
                                tvStudents.setText(String.valueOf(participants));
                                break;
                        }
                    }
                });
            }
        }

        return root;
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
