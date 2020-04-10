package vnu.uet.mobilecourse.assistant.ui.view.course;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import vnu.uet.mobilecourse.assistant.ui.adapter.ClassMateAdapter;
import vnu.uet.mobilecourse.assistant.ui.adapter.GradeAdapter;
import vnu.uet.mobilecourse.assistant.ui.viewmodel.CourseClassmateViewModel;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.ui.viewmodel.CourseGradeViewModel;

public class CourseClassmateFragment extends Fragment {

    private CourseClassmateViewModel viewModel;

    private ClassMateAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_course_classmate, container, false);

        viewModel = new ViewModelProvider(this).get(CourseClassmateViewModel.class);

        initializeClassMateListView(root);

        return root;
    }

    private void initializeClassMateListView(View root) {
        List<String> mates = new ArrayList<>();

        for (int i = 1; i < 20; i++) {
            mates.add("Bạn số " + i);
        }

        adapter = new ClassMateAdapter(mates, this);

        RecyclerView rvClassMate = root.findViewById(R.id.rvClassMate);

        rvClassMate.setAdapter(adapter);
        rvClassMate.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }


}
