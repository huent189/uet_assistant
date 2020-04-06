package vnu.uet.mobilecourse.assistant.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.ui.adapter.AllCoursesAdapter;
import vnu.uet.mobilecourse.assistant.ui.adapter.RecentlyCoursesAdapter;
import vnu.uet.mobilecourse.assistant.ui.model.Course;
import vnu.uet.mobilecourse.assistant.ui.viewmodel.CoursesViewModel;

public class CoursesFragment extends Fragment {

    private RecentlyCoursesAdapter recentlyCoursesAdapter;

    private CoursesViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_courses, container, false);

        viewModel = new ViewModelProvider(this).get(CoursesViewModel.class);

        viewModel.initialize();

        viewModel.getCourses().observe(getViewLifecycleOwner(), new Observer<List<Course>>() {
            @Override
            public void onChanged(List<Course> courses) {
                recentlyCoursesAdapter.notifyDataSetChanged();
            }
        });

        synchronizedScrollView(root);

        initializeRecentlyCoursesView(root);

        initializeAllCoursesView(root);

        return root;
    }

    private void synchronizedScrollView(View root) {
        SynchronizedScrollView svMyCourses = root.findViewById(R.id.svMyCourses);

        TextView tvInvisibleAllCoursesTitle = root.findViewById(R.id.tvInvisibleAllCoursesTitle);
        svMyCourses.setAnchorView(tvInvisibleAllCoursesTitle);

        TextView tvAllCourses = root.findViewById(R.id.tvAllCourses);
        svMyCourses.setSynchronizedView(tvAllCourses);
    }

    private void initializeRecentlyCoursesView(View root) {
        recentlyCoursesAdapter = new RecentlyCoursesAdapter(viewModel.getCourses().getValue(), this);

        ViewPager recentlyCoursesView = root.findViewById(R.id.vpCourseRecently);
        recentlyCoursesView.setAdapter(recentlyCoursesAdapter);
    }

    private void initializeAllCoursesView(View root) {
        List<Course> courses = viewModel.getCourses().getValue();

        courses.addAll(viewModel.getCourses().getValue());

        AllCoursesAdapter allCoursesAdapter = new AllCoursesAdapter(courses, this);

        RecyclerView allCoursesView = root.findViewById(R.id.rvAllCourses);

        allCoursesView.setAdapter(allCoursesAdapter);
        allCoursesView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }
}
