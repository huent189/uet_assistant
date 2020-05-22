package vnu.uet.mobilecourse.assistant.view.course;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vnu.uet.mobilecourse.assistant.viewmodel.CourseGeneralViewModel;
import vnu.uet.mobilecourse.assistant.R;

public class CourseGeneralFragment extends Fragment {

    private CourseGeneralViewModel mViewModel;

    public static CourseGeneralFragment newInstance() {
        return new CourseGeneralFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_general, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CourseGeneralViewModel.class);
        // TODO: Use the ViewModel
    }

}
