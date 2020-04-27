package vnu.uet.mobilecourse.assistant.ui.view.course;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.ui.adapter.FragmentPageAdapter;

public class ExploreCourseFragment extends Fragment {

    private ViewPager vpCourseContent;

    private FragmentPageAdapter pageAdapter;

    private TabLayout tabLayout;

    private Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_explore_course, container, false);

        initializeToolbar(root);

        initializePageAdapter();

        initializeViewPager(root);

        return root;
    }

    private void initializeToolbar(View root) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        if (activity != null) {
            toolbar = root.findViewById(R.id.toolbar);

            activity.setSupportActionBar(toolbar);

            setHasOptionsMenu(true);
        }
    }

    private void initializePageAdapter() {
        FragmentManager fragmentManager = getChildFragmentManager();

        Fragment[] pages = new Fragment[] {
                new CourseProgressFragment(),
                new CourseGradeFragment(),
                new CourseClassmateFragment()
        };

        pageAdapter = new FragmentPageAdapter(fragmentManager, pages);
    }

    private void initializeViewPager(View root) {
        vpCourseContent = root.findViewById(R.id.vpCourseContent);

        vpCourseContent.setOffscreenPageLimit(pageAdapter.getCount());
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

        ViewPager.OnPageChangeListener onPageChangeListener =
                new TabLayout.TabLayoutOnPageChangeListener(tabLayout);

        vpCourseContent.addOnPageChangeListener(onPageChangeListener);
    }

//    private void animateTab() {
//        final int newLeftMargin = <some value>;
//        Animation a = new Animation() {
//
//            @Override
//            protected void applyTransformation(float interpolatedTime, Transformation t) {
//                LayoutParams params = yourView.getLayoutParams();
//                params.leftMargin = (int)(newLeftMargin * interpolatedTime);
//                yourView.setLayoutParams(params);
//            }
//        };
//        a.setDuration(500); // in ms
//        yourView.startAnimation(a);
//    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
