package vnu.uet.mobilecourse.assistant.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.Arrays;
import java.util.List;

public class FragmentPageAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragments;

    private FragmentPageAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    public FragmentPageAdapter(@NonNull FragmentManager fm, Fragment... fragments) {
        this(fm);
        this.mFragments = Arrays.asList(fragments);
    }

    public FragmentPageAdapter(@NonNull FragmentManager fm, List<Fragment> fragments) {
        this(fm);
        this.mFragments = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
