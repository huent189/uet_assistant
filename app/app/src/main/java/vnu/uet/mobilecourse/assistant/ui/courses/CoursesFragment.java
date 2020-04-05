package vnu.uet.mobilecourse.assistant.ui.courses;

import android.animation.ArgbEvaluator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.Course;

public class CoursesFragment extends Fragment {

    private ViewPager viewPager, viewPager2;

    private Adapter adapter;

    List<Course> courses;

    private CoursesViewModel coursesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        coursesViewModel =
                ViewModelProviders.of(this).get(CoursesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_courses, container, false);
//        final TextView textView = root.findViewById(R.id.text_courses);
//        coursesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        courses = new ArrayList<>();

        courses.add(new Course(R.drawable.isometric_course_thumbnail, "Công nghệ phần mềm"));
        courses.add(new Course(R.drawable.isometric_math_course_background, "Công nghệ phần mềm"));
        courses.add(new Course(R.drawable.isometric_course_thumbnail, "Công nghệ phần mềm"));
        courses.add(new Course(R.drawable.isometric_math_course_background, "Công nghệ phần mềm"));
        courses.add(new Course(R.drawable.isometric_course_thumbnail, "Công nghệ phần mềm"));

        adapter = new Adapter(courses, this);

        viewPager = root.findViewById(R.id.vpCourseRecently);
        viewPager.setAdapter(adapter);

        viewPager2 = root.findViewById(R.id.vpAllCourses);
        viewPager2.setAdapter(adapter);
//        viewPager.setPadding(130, 0, 130, 0);

        return root;
    }
}
