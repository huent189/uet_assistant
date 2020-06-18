package vnu.uet.mobilecourse.assistant.view.course;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.viewmodel.ForumViewModel;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.DiscussionAdapter;
import vnu.uet.mobilecourse.assistant.model.Material;
import vnu.uet.mobilecourse.assistant.model.forum.Discussion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ForumFragment extends Fragment {

    private ForumViewModel mViewModel;
    private FragmentActivity mActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(ForumViewModel.class);

        mActivity = getActivity();

        View root = inflater.inflate(R.layout.fragment_forum, container, false);

        RecyclerView rvDiscussions = root.findViewById(R.id.rvDiscussions);
        rvDiscussions.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            Material forumMaterial = getArguments().getParcelable("material");

            if (forumMaterial != null) {
                String forumTitle = forumMaterial.getTitle();

                initializeToolbar(root, forumTitle);

                mViewModel.getDiscussions(forumMaterial.getInstanceId()).observe(getViewLifecycleOwner(), new Observer<List<Discussion>>() {
                    @Override
                    public void onChanged(List<Discussion> discussions) {
                        if (discussions != null) {
                            DiscussionAdapter adapter = new DiscussionAdapter(discussions, forumTitle, ForumFragment.this);
                            rvDiscussions.setAdapter(adapter);
                        }
                    }
                });
            }
        }

        return root;
    }

    private Toolbar initializeToolbar(View root, String title) {
        if (mActivity instanceof AppCompatActivity) {
            Toolbar toolbar = root.findViewById(R.id.toolbar);
            toolbar.setTitle(title);

            ((AppCompatActivity) mActivity).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);

            return toolbar;
        }

        return null;
    }
}
