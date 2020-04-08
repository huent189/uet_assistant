package vnu.uet.mobilecourse.assistant.ui.view.chat;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.ui.adapter.CourseTaskAdapter;
import vnu.uet.mobilecourse.assistant.ui.model.CourseTask;
import vnu.uet.mobilecourse.assistant.ui.model.WeeklyTasks;
import vnu.uet.mobilecourse.assistant.ui.viewmodel.ChatViewModel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatFragment extends Fragment {

    private ChatViewModel chatViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        View root = inflater.inflate(R.layout.fragment_chat, container, false);

        final TextView textView = root.findViewById(R.id.text_chat);

        chatViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.rvTasks);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        //instantiate your adapter with the list of genres
        CourseTask task1 = new CourseTask("Slide xxx", "xxx", 22);
        CourseTask task2 = new CourseTask("Slide xxx", "xxx", 22);
        CourseTask task3 = new CourseTask("Slide xxx", "xxx", 22);
        CourseTask task4 = new CourseTask("Slide xxx", "xxx", 22);
        List<CourseTask> tasks = Arrays.asList(task1, task2, task3, task4);
        WeeklyTasks weeklyTasks = new WeeklyTasks("Week 1", tasks);

        CourseTaskAdapter adapter = new CourseTaskAdapter(Arrays.asList(weeklyTasks), this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        return root;
    }

}
