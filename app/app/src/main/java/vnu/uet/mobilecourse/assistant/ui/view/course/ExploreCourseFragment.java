package vnu.uet.mobilecourse.assistant.ui.view.course;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.ui.adapter.FragmentPageAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class ExploreCourseFragment extends Fragment {

    private ViewPager vpCourseContent;

    private FragmentPageAdapter pageAdapter;

    private TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_explore_course, container, false);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        Fragment[] pages = new Fragment[] {
                new CourseProgressFragment(),
                new CourseGradeFragment(),
                new CourseClassmateFragment()
        };

        pageAdapter = new FragmentPageAdapter(fragmentManager, pages);

        vpCourseContent = root.findViewById(R.id.vpCourseContent);
        vpCourseContent.setOffscreenPageLimit(pages.length);
        vpCourseContent.setAdapter(pageAdapter);

        tabLayout = root.findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vpCourseContent.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        vpCourseContent
                .addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
