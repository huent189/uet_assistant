package vnu.uet.mobilecourse.assistant.view.course;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.DiscussionAdapter;
import vnu.uet.mobilecourse.assistant.model.Material;
import vnu.uet.mobilecourse.assistant.model.forum.Discussion;
import vnu.uet.mobilecourse.assistant.viewmodel.ForumViewModel;

public class ForumFragment extends Fragment {

    private ForumViewModel mViewModel;
    private FragmentActivity mActivity;
    private Bundle mRecyclerViewState;
    private RecyclerView mRvDiscussions;
    private DiscussionAdapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(ForumViewModel.class);

        mActivity = getActivity();

        View root = inflater.inflate(R.layout.fragment_forum, container, false);

        mRvDiscussions = root.findViewById(R.id.rvDiscussions);
        mRvDiscussions.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            Material forumMaterial = getArguments().getParcelable("material");

            if (forumMaterial != null) {
                String forumTitle = forumMaterial.getTitle();

                initializeToolbar(root, forumTitle);

                View layoutEmpty = root.findViewById(R.id.layoutEmpty);

                ShimmerFrameLayout sflDiscussions = root.findViewById(R.id.sflDiscussions);
                sflDiscussions.startShimmerAnimation();

                int instanceId = forumMaterial.getInstanceId();

                mViewModel.getDiscussions(instanceId).observe(getViewLifecycleOwner(), stateModel -> {
                    switch (stateModel.getStatus()) {
                        case LOADING:
                        case ERROR:
                            sflDiscussions.setVisibility(View.VISIBLE);
                            layoutEmpty.setVisibility(View.GONE);
                            mRvDiscussions.setVisibility(View.GONE);

                            break;

                        case SUCCESS:
                            Log.d(ForumFragment.class.getSimpleName(), "onChanged: discussions");

                            List<Discussion> discussions = stateModel.getData();

                            if (discussions.isEmpty()) {
                                sflDiscussions.setVisibility(View.GONE);
                                layoutEmpty.setVisibility(View.VISIBLE);
                                mRvDiscussions.setVisibility(View.GONE);
                            } else {
                                sflDiscussions.setVisibility(View.GONE);
                                layoutEmpty.setVisibility(View.GONE);
                                mRvDiscussions.setVisibility(View.VISIBLE);

                                mAdapter = new DiscussionAdapter(discussions,
                                        forumTitle, ForumFragment.this);
                                mRvDiscussions.setAdapter(mAdapter);
                            }

                            restoreRecycleViewState();

                            break;
                    }
                });
            }
        }

        return root;
    }

    private static final String KEY_RECYCLER_STATE = DiscussionAdapter.class.getName();

    public void saveRecycleViewState() {
        RecyclerView.LayoutManager layoutManager = mRvDiscussions.getLayoutManager();

        if (layoutManager != null) {
            mRecyclerViewState = new Bundle();

            Parcelable onSaveInstanceState = layoutManager.onSaveInstanceState();
            mRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, onSaveInstanceState);
        }
    }

    private void restoreRecycleViewState() {
        RecyclerView.LayoutManager layoutManager = mRvDiscussions.getLayoutManager();

        if (mRecyclerViewState != null && layoutManager != null) {
            Parcelable onSaveInstanceState = mRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            layoutManager.onRestoreInstanceState(onSaveInstanceState);
        }
    }

    private void initializeToolbar(View root, String title) {
        if (mActivity instanceof AppCompatActivity) {
            Toolbar toolbar = root.findViewById(R.id.toolbar);
            toolbar.setTitle(title);

            ((AppCompatActivity) mActivity).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);
        }
    }

    public ForumViewModel getViewModel() {
        return mViewModel;
    }
}
