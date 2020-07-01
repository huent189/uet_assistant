package vnu.uet.mobilecourse.assistant.view.course;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.CourseGeneralMaterialAdapter;
import vnu.uet.mobilecourse.assistant.adapter.CourseSessionAdapter;
import vnu.uet.mobilecourse.assistant.exception.SQLiteRecordNotFound;
import vnu.uet.mobilecourse.assistant.model.Course;
import vnu.uet.mobilecourse.assistant.model.FinalExam;
import vnu.uet.mobilecourse.assistant.model.ICourse;
import vnu.uet.mobilecourse.assistant.model.Material;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseInfo;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseSession;
import vnu.uet.mobilecourse.assistant.repository.course.PortalRepository;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.util.FbAndCourseMap;
import vnu.uet.mobilecourse.assistant.util.StringConst;
import vnu.uet.mobilecourse.assistant.viewmodel.CourseGeneralViewModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

import static vnu.uet.mobilecourse.assistant.model.material.CourseConstant.MaterialType.GENERAL;

public class CourseGeneralFragment extends Fragment {

    private CourseGeneralViewModel mViewModel;
    private RecyclerView mRvGeneralMaterials;
    private NestedScrollView mScrollView;
    private Bundle mScrollViewState;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(CourseGeneralViewModel.class);

        View root = inflater.inflate(R.layout.fragment_course_general, container, false);
        mScrollView = root.findViewById(R.id.scrollView);

        Bundle args = getArguments();
        if (args != null) {
            ICourse course = args.getParcelable("course");
            assert course != null;

            String courseCode = course.getCode();
            courseCode = courseCode
                    .replace(StringConst.COURSE_PREFIX, StringConst.EMPTY)
                    .replace(StringConst.UNDERSCORE_CHAR, StringConst.SPACE_CHAR)
                    .trim();

            TextView tvCourseTitle = root.findViewById(R.id.tvCourseTitle);
            tvCourseTitle.setText(course.getTitle());

            TextView tvCourseId = root.findViewById(R.id.tvCourseId);
            tvCourseId.setText(course.getCode());

            setupFinalExam(root, course.getCode());

            TextView tvCredits = root.findViewById(R.id.tvCredits);

            CircularProgressBar cpbProgress = root.findViewById(R.id.cpbProgress);
            TextView tvProgress = root.findViewById(R.id.tvProgress);

            if (course instanceof Course) {
                Course cast = (Course) course;
                mViewModel.getProgress(cast.getId()).observe(getViewLifecycleOwner(), p ->{
                    float progress = p.floatValue();
                    tvProgress.setText(String.format(Locale.ROOT, "%.0f%%", progress));
                    cpbProgress.setProgressWithAnimation(progress);
                });
            }

            initializeGeneralMaterialsView(root, course);

            RecyclerView rvSessions = root.findViewById(R.id.rvSessions);

            if (!courseCode.isEmpty()) {
                initializeSessionsView(rvSessions);
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
            } else {
                LinearLayout layoutContainerTop = root.findViewById(R.id.layout_basic_info);
                layoutContainerTop.setVisibility(View.GONE);

                LinearLayout layoutContainerBottom = root.findViewById(R.id.layout_common_info);
                layoutContainerBottom.setVisibility(View.GONE);

                TextView tvSessionTitle = root.findViewById(R.id.tvSessionTitle);
                tvSessionTitle.setVisibility(View.GONE);

                rvSessions.setVisibility(View.GONE);
            }
        }

        return root;
    }

    private void setupFinalExam(View root, String code) {
        CardView cvFinalExam = root.findViewById(R.id.cvFinalExam);
        String cleanCode = FbAndCourseMap.cleanCode(code);

        IStateLiveData<FinalExam> liveData = new PortalRepository().getFinalExamByCourse(cleanCode);
        liveData.observe(getViewLifecycleOwner(), new Observer<StateModel<FinalExam>>() {
            @Override
            public void onChanged(StateModel<FinalExam> stateModel) {
                switch (stateModel.getStatus()) {
                    case SUCCESS:
                        bindFinalExam(stateModel.getData(), cvFinalExam);
                        break;

                    case ERROR:
                        if (stateModel.getError() instanceof SQLiteRecordNotFound) {
                            liveData.removeObserver(this);

                            StringBuilder builder = new StringBuilder(cleanCode);
                            builder.insert(FbAndCourseMap.DELIMITER_POS, StringConst.SPACE);

                            new PortalRepository().getFinalExamByCourse(builder.toString())
                                    .observe(getViewLifecycleOwner(), new Observer<StateModel<FinalExam>>() {
                                        @Override
                                        public void onChanged(StateModel<FinalExam> stateModel) {
                                            switch (stateModel.getStatus()) {
                                                case SUCCESS:
                                                    bindFinalExam(stateModel.getData(), cvFinalExam);
                                                    break;

                                                default:
                                                    cvFinalExam.setVisibility(View.GONE);
                                                    break;
                                            }
                                        }
                                    });

                        } else {
                            cvFinalExam.setVisibility(View.GONE);
                        }

                        break;

                    default:
                        cvFinalExam.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    private void bindFinalExam(FinalExam exam, CardView cvFinalExam) {
        TextView tvTime = cvFinalExam.findViewById(R.id.tvTime);
        TextView tvRoom = cvFinalExam.findViewById(R.id.tvRoom);
        TextView tvFormat = cvFinalExam.findViewById(R.id.tvFormat);
        TextView tvSBD = cvFinalExam.findViewById(R.id.tvSBD);

        cvFinalExam.setVisibility(View.VISIBLE);
        tvTime.setText(DateTimeUtils.DATE_TIME_FORMAT.format(exam.getTime()));

        String place = exam.getPlace();
        tvRoom.setText(place);

        String form = exam.getForm();
        if (form.isEmpty()) form = "Không có";
        tvFormat.setText(form);

        tvSBD.setText(exam.getIdNumber());
    }

    private void initializeGeneralMaterialsView(View root, ICourse course) {
        mRvGeneralMaterials = root.findViewById(R.id.rvGeneralMaterials);
        mRvGeneralMaterials.setLayoutManager(new LinearLayoutManager(getContext()));

        TextView tvGeneralMaterials = root.findViewById(R.id.tvGeneralMaterials);

        if (course instanceof Course) {
            int courseId = ((Course) course).getId();

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
                                            new CourseGeneralMaterialAdapter(materials,
                                                    CourseGeneralFragment.this);

                                    mRvGeneralMaterials.setAdapter(adapter);
                                }
                            });
                } else {
                    tvGeneralMaterials.setVisibility(View.GONE);
                }
            });

        } else {
            tvGeneralMaterials.setVisibility(View.GONE);
            mRvGeneralMaterials.setVisibility(View.GONE);
        }
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

    private void initializeSessionsView(RecyclerView rvSessions) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false);

        rvSessions.setLayoutManager(layoutManager);
    }

    @Override
    public void onPause() {
        super.onPause();

        saveRecycleViewState();
    }

    @Override
    public void onResume() {
        super.onResume();

        restoreRecycleViewState();
    }

    private void saveRecycleViewState() {
        RecyclerView.LayoutManager layoutManager = mRvGeneralMaterials.getLayoutManager();

        if (layoutManager != null) {
            mScrollViewState = new Bundle();

            int scrollY = mScrollView.getScrollY();
            Log.d(CourseGeneralFragment.class.getSimpleName(), "saveRecycleViewState: " + scrollY);
            mScrollViewState.putInt(KEY_SCROLL_Y, scrollY);

            Parcelable onSaveInstanceState = layoutManager.onSaveInstanceState();
            mScrollViewState.putParcelable(KEY_RECYCLER_STATE, onSaveInstanceState);
        }
    }

    private void restoreRecycleViewState() {
        RecyclerView.LayoutManager layoutManager = mRvGeneralMaterials.getLayoutManager();

        if (mScrollViewState != null && layoutManager != null) {
            mScrollView.setVisibility(View.INVISIBLE);

            Parcelable onSaveInstanceState = mScrollViewState.getParcelable(KEY_RECYCLER_STATE);
            layoutManager.onRestoreInstanceState(onSaveInstanceState);

            int scrollY = mScrollViewState.getInt(KEY_SCROLL_Y);
            Log.d(CourseGeneralFragment.class.getSimpleName(), "restoreRecycleViewState: " + scrollY);

            mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mScrollView.smoothScrollTo(0, scrollY);
                    mScrollView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private static final String KEY_SCROLL_Y = "scrollY";
    private static final String KEY_RECYCLER_STATE = CourseGeneralMaterialAdapter.class.getName();
}
