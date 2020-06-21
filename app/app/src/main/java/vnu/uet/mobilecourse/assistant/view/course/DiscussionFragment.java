package vnu.uet.mobilecourse.assistant.view.course;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.InternalResourceAdapter;
import vnu.uet.mobilecourse.assistant.model.forum.Discussion;
import vnu.uet.mobilecourse.assistant.model.forum.Post;
import vnu.uet.mobilecourse.assistant.model.material.InternalFile;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.util.DimensionUtils;
import vnu.uet.mobilecourse.assistant.util.StringUtils;
import vnu.uet.mobilecourse.assistant.view.component.RepliesView;
import vnu.uet.mobilecourse.assistant.viewmodel.DiscussionViewModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class DiscussionFragment extends Fragment {

    private DiscussionViewModel mViewModel;
    private FragmentActivity mActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(DiscussionViewModel.class);

        mActivity = getActivity();

        View root = inflater.inflate(R.layout.fragment_discussion, container, false);

        if (getArguments() != null) {
            String forumTitle = getArguments().getString("forum");
            if (forumTitle != null) {
                initializeToolbar(root, forumTitle);
            }

            Discussion discussion = getArguments().getParcelable("discussion");
            if (discussion != null) {
                TextView tvPinned = root.findViewById(R.id.tvPinned);
                tvPinned.setVisibility(discussion.isPinned() ? View.VISIBLE : View.GONE);

                TextView tvAuthorName = root.findViewById(R.id.tvAuthorName);
                tvAuthorName.setText(discussion.getAuthorName());

                TextView tvCreatedTime = root.findViewById(R.id.tvCreatedTime);
                String createdTime = DateTimeUtils.generateViewText(discussion.getTimeCreated());
                tvCreatedTime.setText(createdTime);

                TextView tvTitle = root.findViewById(R.id.tvTitle);
                tvTitle.setText(discussion.getName());

                ImageView ivStarred = root.findViewById(R.id.ivStarred);
                if (discussion.isStarred()) {
                    ivStarred.setImageResource(R.drawable.ic_star_border_24dp);
                } else {
                    ivStarred.setImageResource(R.drawable.ic_star_border_24dp);
                }

                setupFollowButton(root, discussion);
                setupRepliesView(root, discussion);

            }
        }

        setupContainerView(root);

        return root;
    }

    private void setupContainerView(View root) {
        NestedScrollView scrollView = root.findViewById(R.id.scrollView);
        View layoutContainer = root.findViewById(R.id.layoutContainer);

        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Ensure you call it only once
                scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Get visible height of scroll view
                Rect rect = new Rect();
                scrollView.getLocalVisibleRect(rect);
                int height = rect.height() - DimensionUtils.dpToPx(16, mActivity);

                // Set min height
                layoutContainer.setMinimumHeight(height);
            }
        });
    }



    private void setupRepliesView(View root, Discussion discussion) {
        // update replies counter
        TextView tvReplies = root.findViewById(R.id.tvReplies);
        tvReplies.setText(String.valueOf(discussion.getNumberReplies()));

        // initialize empty replies layout
        View layoutEmpty = root.findViewById(R.id.layoutEmpty);

        // initialize replies panel layout
        RepliesView repliesView = root.findViewById(R.id.repliesView);

        // initialize root post layout
        View layoutRootPost = root.findViewById(R.id.layoutRootPost);

        // initialize shimmer loading layout
        ShimmerFrameLayout sflReplies = root.findViewById(R.id.sflReplies);
        sflReplies.startShimmerAnimation();

        // initialize root post content text view
        TextView tvContent = root.findViewById(R.id.tvContent);

        // initialize root post attachments list view
        RecyclerView rvAttachments = root.findViewById(R.id.rvAttachments);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity,
                LinearLayoutManager.HORIZONTAL, false);
        rvAttachments.setLayoutManager(layoutManager);

        mViewModel.getDiscussionDetail(discussion.getId()).observe(getViewLifecycleOwner(), new Observer<Post>() {
            @Override
            public void onChanged(Post post) {
                // success
                if (post != null) {
                    layoutRootPost.setVisibility(View.VISIBLE);
                    sflReplies.setVisibility(View.GONE);

                    SpannableStringBuilder rootContent = StringUtils.convertHtml(post.getMessage());
                    tvContent.setText(rootContent);

                    repliesView.setRootPost(post);

                    root.setBackgroundResource(R.color.backgroundLight);

                    List<InternalFile> files = post.getAttachments();
                    Log.d(getClass().getSimpleName(), "attachments: " + (files == null ? "null" : files.size()));
                    if (files != null && !files.isEmpty()) {
                        InternalResourceAdapter adapter = new InternalResourceAdapter(files, mActivity);
                        rvAttachments.setAdapter(adapter);
                    }

                    if (discussion.getNumberReplies() == 0) {
                        layoutEmpty.setVisibility(View.VISIBLE);
                        repliesView.setVisibility(View.GONE);
                    } else {
                        repliesView.setVisibility(View.VISIBLE);
                        layoutEmpty.setVisibility(View.GONE);
                    }

                } else {
                    repliesView.setVisibility(View.GONE);
                    layoutRootPost.setVisibility(View.GONE);
                    layoutEmpty.setVisibility(View.GONE);
                    sflReplies.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setupFollowButton(View root, Discussion discussion) {
        Button btnFollow = root.findViewById(R.id.btnFollow);
        if (discussion.isInterest()) {
            updateFollowEffect(btnFollow);
        } else {
            updateUnFollowEffect(btnFollow);
        }

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int discussionId = discussion.getId();
                LifecycleOwner lifecycleOwner = getViewLifecycleOwner();

                if (discussion.isInterest()) {
                    updateUnFollowEffect(btnFollow);

                    mViewModel.unFollow(discussionId).observe(lifecycleOwner, state -> {
                        // recover in case catch a error
                        if (state.getStatus() == StateStatus.ERROR) {
                            Toast.makeText(mActivity,
                                    "Hủy theo dõi thất bại",
                                    Toast.LENGTH_SHORT
                            ).show();
                            updateFollowEffect(btnFollow);
                        }
                    });
                } else {
                    updateFollowEffect(btnFollow);

                    mViewModel.follow(discussionId).observe(lifecycleOwner, state -> {
                        // recover in case catch a error
                        if (state.getStatus() == StateStatus.ERROR) {
                            Toast.makeText(mActivity,
                                    "Theo dõi thất bại",
                                    Toast.LENGTH_SHORT
                            ).show();
                            updateUnFollowEffect(btnFollow);
                        }
                    });
                }
            }
        });
    }

    private void updateFollowEffect(Button btnFollow) {
        btnFollow.setText(R.string.title_discussion_unfollow);

        int color = ContextCompat.getColor(mActivity, R.color.primary);
        btnFollow.setTextColor(color);

        btnFollow.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_notifications_active_24dp, 0, 0, 0);

    }

    private void updateUnFollowEffect(Button btnFollow) {
        btnFollow.setText(R.string.title_discussion_follow);

        int color = ContextCompat.getColor(mActivity, R.color.primaryDark);
        btnFollow.setTextColor(color);

        btnFollow.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_add_alert_24dp, 0, 0, 0);
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
