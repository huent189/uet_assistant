package vnu.uet.mobilecourse.assistant.ui.view.course;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.ui.adapter.GradeAdapter;
import vnu.uet.mobilecourse.assistant.ui.viewmodel.CourseGradeViewModel;

public class CourseGradeFragment extends Fragment {

    private CourseGradeViewModel viewModel;

    private GradeAdapter gradeAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_course_grade, container, false);

        viewModel = new ViewModelProvider(this).get(CourseGradeViewModel.class);

        initializeGradeListView(root);

        return root;
    }

    private void initializeGradeListView(View root) {
        List<String> testes = new ArrayList<>();

        for (int i = 1; i < 20; i++) {
            testes.add("Bài kiểm tra " + i);
        }

        gradeAdapter = new GradeAdapter(testes, this);

        RecyclerView rvGrades = root.findViewById(R.id.rvGrades);

        rvGrades.setAdapter(gradeAdapter);
        rvGrades.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }
}
