package vnu.uet.mobilecourse.assistant.ui.courses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.ui.adapter.RecentlyCoursesAdapter;
import vnu.uet.mobilecourse.assistant.ui.model.Course;
import vnu.uet.mobilecourse.assistant.ui.viewmodel.CoursesViewModel;

public class CoursesFragment extends Fragment {

    private ViewPager viewPager, viewPager2;

    private RecentlyCoursesAdapter adapter;

    private CoursesViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_courses, container, false);

        viewModel = new ViewModelProvider(this).get(CoursesViewModel.class);

        viewModel.initialize();

        viewModel.getCourses().observe(getViewLifecycleOwner(), new Observer<List<Course>>() {
            @Override
            public void onChanged(List<Course> courses) {
                adapter.notifyDataSetChanged();
            }
        });

        initializeRecentlyCoursesView(root);

        return root;
    }

    private void initializeRecentlyCoursesView(View root) {
        adapter = new RecentlyCoursesAdapter(viewModel.getCourses().getValue(), this);

        viewPager = root.findViewById(R.id.vpCourseRecently);
        viewPager.setAdapter(adapter);

        viewPager2 = root.findViewById(R.id.vpAllCourses);
        viewPager2.setAdapter(adapter);
    }
}
