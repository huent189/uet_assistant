package vnu.uet.mobilecourse.assistant.ui.view.course;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vnu.uet.mobilecourse.assistant.ui.viewmodel.CourseClassmateViewModel;
import vnu.uet.mobilecourse.assistant.R;

public class CourseClassmateFragment extends Fragment {

    private CourseClassmateViewModel mViewModel;

    public static CourseClassmateFragment newInstance() {
        return new CourseClassmateFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_classmate, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CourseClassmateViewModel.class);
        // TODO: Use the ViewModel
    }

}
