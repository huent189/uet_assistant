package vnu.uet.mobilecourse.assistant.view.course;

import androidx.lifecycle.Observer;
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

import java.util.List;

import vnu.uet.mobilecourse.assistant.model.CourseContent;
import vnu.uet.mobilecourse.assistant.repository.CourseRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.CourseProgressViewModel;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.CourseContentAdapter;

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
        recyclerView.setLayoutManager(layoutManager);

        Bundle args = getArguments();

        if (args != null) {
            int courseId = args.getInt("courseId");

            Fragment thisFragment = this;

            CourseRepository.getInstance().getContent(courseId).observe(getViewLifecycleOwner(), new Observer<List<CourseContent>>() {
                @Override
                public void onChanged(List<CourseContent> contents) {
                    CourseContentAdapter adapter = new CourseContentAdapter(contents, thisFragment);
                    recyclerView.setAdapter(adapter);
                }
            });
        }

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CourseProgressViewModel.class);
        // TODO: Use the ViewModel
    }
}
