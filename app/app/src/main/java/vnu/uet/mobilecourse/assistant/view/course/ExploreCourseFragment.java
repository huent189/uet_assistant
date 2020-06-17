package vnu.uet.mobilecourse.assistant.view.course;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.FragmentPageAdapter;
import vnu.uet.mobilecourse.assistant.model.ICourse;

public class ExploreCourseFragment extends Fragment {

    private ViewPager mVpCourseContent;
    private FragmentPageAdapter mPageAdapter;
    private String mCourseTitle;
    private String mCourseCode;
    private int mThumbnail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_explore_course, container, false);

        // restore expandable toolbar state
        CoordinatorLayout coordinatorLayout = root.findViewById(R.id.coordinator_layout);
        ViewCompat.requestApplyInsets(coordinatorLayout);

        Bundle args = getArguments();

        if (args != null) {
            ICourse course = args.getParcelable("course");

            assert course != null;
            mCourseTitle = course.getTitle();
            mCourseCode = course.getCode();
            mThumbnail = course.getThumbnail();
        }

        initializeToolbar(root);

        initializePageAdapter();

        initializeViewPager(root);

        return root;
    }

    private void initializeToolbar(View root) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        ImageView ivThumbnail = root.findViewById(R.id.ivThumbnail);
        ivThumbnail.setImageResource(mThumbnail);

        if (activity != null) {
            Toolbar toolbar = root.findViewById(R.id.toolbar);

            toolbar.setTitle(mCourseTitle);

            activity.setSupportActionBar(toolbar);

            setHasOptionsMenu(true);
        }
    }

    private void initializePageAdapter() {
        FragmentManager fragmentManager = getChildFragmentManager();

        Fragment[] pages;

        if (mCourseCode == null || mCourseCode.isEmpty()) {
            pages = new Fragment[] {
                    new CourseGeneralFragment()
            };
        } else  {
            pages = new Fragment[] {
                    new CourseGeneralFragment(),
                    new CourseProgressFragment(),
                    new CourseGradeFragment(),
                    new CourseClassmateFragment()
            };
        }

        for (Fragment page : pages) {
            Bundle bundle = getArguments();
            page.setArguments(bundle);
        }

        mPageAdapter = new FragmentPageAdapter(fragmentManager, pages);
    }

    private void initializeViewPager(View root) {
        mVpCourseContent = root.findViewById(R.id.vpCourseContent);

        mVpCourseContent.setOffscreenPageLimit(mPageAdapter.getCount());
        mVpCourseContent.setAdapter(mPageAdapter);

        TabLayout tabLayout = root.findViewById(R.id.tabLayout);

        if (mCourseCode == null || mCourseCode.isEmpty()) {
            while (tabLayout.getTabCount() > 1) {
                tabLayout.removeTabAt(1);
            }

            tabLayout.setVisibility(View.GONE);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mVpCourseContent.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        ViewPager.OnPageChangeListener onPageChangeListener =
                new TabLayout.TabLayoutOnPageChangeListener(tabLayout);

        mVpCourseContent.addOnPageChangeListener(onPageChangeListener);
    }
}
