package vnu.uet.mobilecourse.assistant.ui.view.course;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
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
import vnu.uet.mobilecourse.assistant.ui.view.SynchronizedScrollView;
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

        final Button btnSearch = root.findViewById(R.id.btnSearch);
        final TextView tvAllCourses = root.findViewById(R.id.tvAllCourses);
        final SearchView searchView = root.findViewById(R.id.searchView);
        final ImageView ivSearchIcon = root.findViewById(R.id.ivSearchIcon);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isVisible = tvAllCourses.getVisibility() == View.VISIBLE;

                if (isVisible) {
                    searchView.setVisibility(View.VISIBLE);
                    searchView.requestFocus();
//                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                    tvAllCourses.setVisibility(View.INVISIBLE);
                    ivSearchIcon.setImageResource(R.drawable.ic_close_24dp);
                } else {
                    tvAllCourses.setVisibility(View.VISIBLE);
                    searchView.setVisibility(View.INVISIBLE);
                    ivSearchIcon.setImageResource(R.drawable.ic_search_24dp);
                }
            }
        });

        synchronizedScrollView(root);

        initializeRecentlyCoursesView(root);

        initializeAllCoursesView(root);

        return root;
    }

    private void synchronizedScrollView(View root) {
        SynchronizedScrollView svMyCourses = root.findViewById(R.id.svMyCourses);

        View tvInvisibleAllCoursesTitle = root.findViewById(R.id.tvInvisibleAllCoursesTitle);
        svMyCourses.setAnchorView(tvInvisibleAllCoursesTitle);

        View clAllCoursesToolbar = root.findViewById(R.id.clAllCoursesToolbar);
        svMyCourses.setSynchronizedView(clAllCoursesToolbar);
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
