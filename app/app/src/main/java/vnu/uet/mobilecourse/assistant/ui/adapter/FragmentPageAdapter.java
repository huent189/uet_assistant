package vnu.uet.mobilecourse.assistant.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.Arrays;
import java.util.List;

public class FragmentPageAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    private FragmentPageAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    public FragmentPageAdapter(@NonNull FragmentManager fm, Fragment... fragments) {
        this(fm);
        this.fragments = Arrays.asList(fragments);
    }

    public FragmentPageAdapter(@NonNull FragmentManager fm, List<Fragment> fragments) {
        this(fm);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
