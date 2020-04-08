package vnu.uet.mobilecourse.assistant.ui.view.course;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vnu.uet.mobilecourse.assistant.ui.viewmodel.CourseGradeViewModel;
import vnu.uet.mobilecourse.assistant.R;

public class CourseGradeFragment extends Fragment {

    private CourseGradeViewModel mViewModel;

    public static CourseGradeFragment newInstance() {
        return new CourseGradeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_grade, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CourseGradeViewModel.class);
        // TODO: Use the ViewModel
    }

}
