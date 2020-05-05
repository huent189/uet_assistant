package vnu.uet.mobilecourse.assistant.view.course;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

import vnu.uet.mobilecourse.assistant.viewmodel.CourseProgressViewModel;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.CourseTaskAdapter;
import vnu.uet.mobilecourse.assistant.model.CourseTask;
import vnu.uet.mobilecourse.assistant.model.WeeklyTasks;

public class CourseProgressFragment extends Fragment {

    private CourseProgressViewModel mViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_course_progress, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.rvTasks);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        //instantiate your adapter with the list of genres
        CourseTask task1 = new CourseTask("Slide xxx", "xxx", 22);
        CourseTask task2 = new CourseTask("Slide xxx", "xxx", 22);
        CourseTask task3 = new CourseTask("Slide xxx", "xxx", 22);
        CourseTask task4 = new CourseTask("Slide xxx", "xxx", 22);
        List<CourseTask> tasks = Arrays.asList(task1, task2, task3, task4);
        WeeklyTasks weeklyTasks = new WeeklyTasks("Week 1", tasks);

        CourseTaskAdapter adapter = new CourseTaskAdapter(Arrays.asList(weeklyTasks, weeklyTasks), this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CourseProgressViewModel.class);
        // TODO: Use the ViewModel
    }
}