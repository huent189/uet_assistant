package vnu.uet.mobilecourse.assistant.ui.view.course;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.ui.adapter.CourseTaskAdapter;
import vnu.uet.mobilecourse.assistant.ui.model.CourseTask;
import vnu.uet.mobilecourse.assistant.ui.model.WeeklyTasks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExploreCourseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_explore_course, container, false);


        RecyclerView recyclerView = root.findViewById(R.id.rvTasks);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        //instantiate your adapter with the list of genres
        CourseTask task1 = new CourseTask("Slide xxx", "xxx", 22);
        CourseTask task2 = new CourseTask("Slide xxx", "xxx", 22);
        CourseTask task3 = new CourseTask("Slide xxx", "xxx", 22);
        CourseTask task4 = new CourseTask("Slide xxx", "xxx", 22);
        List<CourseTask> tasks = Arrays.asList(task1, task2, task3, task4);
        WeeklyTasks weeklyTasks = new WeeklyTasks("Week 1", tasks);

        CourseTaskAdapter adapter = new CourseTaskAdapter(Collections.singletonList(weeklyTasks), this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        return root;
    }
}
