package vnu.uet.mobilecourse.assistant.view.course;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vnu.uet.mobilecourse.assistant.database.DAO.CourseInfoDAO;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseInfo;
import vnu.uet.mobilecourse.assistant.util.CONST;
import vnu.uet.mobilecourse.assistant.viewmodel.CourseGeneralViewModel;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class CourseGeneralFragment extends Fragment {

    private CourseGeneralViewModel mViewModel;

    public static CourseGeneralFragment newInstance() {
        return new CourseGeneralFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        String id = getArguments().getString("courseCode");
        assert id != null;
        id = id.replace(CONST.COURSE_PREFIX + CONST.UNDERSCORE, "").replace(CONST.UNDERSCORE, CONST.SPACE);

        new CourseInfoDAO().read(id).observe(getViewLifecycleOwner(), new Observer<StateModel<CourseInfo>>() {
            @Override
            public void onChanged(StateModel<CourseInfo> stateModel) {
                switch (stateModel.getStatus()) {
                    case SUCCESS:

                        CourseInfo courseInfo = stateModel.getData();
                        System.out.println(courseInfo.getId());
                        System.out.println(courseInfo.getSessions());
                }
            }
        });

        return inflater.inflate(R.layout.fragment_course_general, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CourseGeneralViewModel.class);
        // TODO: Use the ViewModel
    }

}
