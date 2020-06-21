package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.forum.Discussion;
import vnu.uet.mobilecourse.assistant.model.forum.InterestedDiscussion;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.view.course.ForumFragment;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class DiscussionAdapter extends RecyclerView.Adapter<DiscussionAdapter.DiscussionHolder> {

    private List<Discussion> mDiscussions;
    private ForumFragment mOwner;
    private String mForumTitle;

    public DiscussionAdapter(List<Discussion> discussions, String forumTitle, ForumFragment owner) {
        this.mDiscussions = discussions;
        this.mOwner = owner;
        this.mForumTitle = forumTitle;
    }

    @NonNull
    @Override
    public DiscussionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mOwner.getLayoutInflater()
                .inflate(R.layout.layout_discussion_item, parent, false);

        return new DiscussionHolder(view) {

            @Override
            protected IStateLiveData<InterestedDiscussion> onFollow(int discussionId) {
                mOwner.saveRecycleViewState();
                return mOwner.getViewModel().follow(discussionId);
            }

            @Override
            protected IStateLiveData<String> onUnFollow(int discussionId) {
                mOwner.saveRecycleViewState();
                return mOwner.getViewModel().unFollow(discussionId);
            }
        };
    }

    @Override
    public void onBindViewHolder(@NonNull DiscussionHolder holder, int position) {
        final Discussion notification = mDiscussions.get(position);
        holder.bind(notification, mForumTitle, mOwner);
    }

    @Override
    public int getItemCount() {
        return mDiscussions.size();
    }

    static abstract class DiscussionHolder extends RecyclerView.ViewHolder {

        private View mLayoutContainer;
        private TextView mTvPinned;
        private CircleImageView mCivAvatar;
        private TextView mTvAuthorName;

        private TextView mTvCreatedTime;
        private TextView mTvTitle;
        private ImageView mIvStarred;
        private TextView mTvReplies;
        private Button mBtnFollow;

        private Fragment mOwner;
        private NavController mNavController;

        public DiscussionHolder(@NonNull View view) {
            super(view);

            mLayoutContainer = view.findViewById(R.id.layoutContainer);

            mTvPinned = view.findViewById(R.id.tvPinned);
            mCivAvatar = view.findViewById(R.id.civAvatar);
            mTvAuthorName = view.findViewById(R.id.tvAuthorName);
            mTvCreatedTime = view.findViewById(R.id.tvCreatedTime);
            mTvTitle = view.findViewById(R.id.tvTitle);
            mIvStarred = view.findViewById(R.id.ivStarred);
            mTvReplies = view.findViewById(R.id.tvReplies);
            mBtnFollow = view.findViewById(R.id.btnFollow);

        }

        public void bind(Discussion discussion, String forumTitle, Fragment owner) {
            mOwner = owner;

            Activity activity = mOwner.getActivity();

            if (activity != null) {
                mNavController = Navigation
                        .findNavController(activity, R.id.nav_host_fragment);
            }

            Log.d("DISCUSSION", discussion.toString());

            if (discussion.isPinned()) {
                mTvPinned.setVisibility(View.VISIBLE);
                mLayoutContainer.setBackgroundResource(R.color.backgroundLight);
            } else {
                mTvPinned.setVisibility(View.GONE);
                mLayoutContainer.setBackgroundResource(0);
            }

            mTvAuthorName.setText(discussion.getAuthorName());
            String time = DateTimeUtils.generateViewText(discussion.getTimeCreated());
            mTvCreatedTime.setText(time);
            mTvTitle.setText(discussion.getName());
            mTvReplies.setText(String.valueOf(discussion.getNumberReplies()));

            if (discussion.isStarred()) {
                mIvStarred.setImageResource(R.drawable.ic_star_24dp);
            } else {
                mIvStarred.setImageResource(R.drawable.ic_star_border_24dp);
            }

            if (discussion.isInterest()) {
                updateFollowEffect();
            } else {
                updateUnFollowEffect();
            }

            mBtnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int discussionId = discussion.getId();
                    LifecycleOwner lifecycleOwner = mOwner.getViewLifecycleOwner();

                    if (discussion.isInterest()) {
                        updateUnFollowEffect();

                        onUnFollow(discussionId).observe(lifecycleOwner, new Observer<StateModel<String>>() {
                            @Override
                            public void onChanged(StateModel<String> state) {
                                // recover in case catch a error
                                if (state.getStatus() == StateStatus.ERROR) {
                                    Toast.makeText(mOwner.getContext(),
                                            "Hủy theo dõi thất bại - " + state.getError().getMessage(),
                                            Toast.LENGTH_SHORT
                                    ).show();
                                    updateFollowEffect();
                                }
                            }
                        });
                    } else {
                        updateFollowEffect();

                        onFollow(discussionId).observe(lifecycleOwner, new Observer<StateModel<InterestedDiscussion>>() {
                            @Override
                            public void onChanged(StateModel<InterestedDiscussion> state) {
                                // recover in case catch a error
                                if (state.getStatus() == StateStatus.ERROR) {
                                    Toast.makeText(mOwner.getContext(),
                                            "Theo dõi thất bại - " + state.getError().getMessage(),
                                            Toast.LENGTH_SHORT
                                    ).show();
                                    updateUnFollowEffect();
                                }
                            }
                        });
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("discussion", discussion);
                    bundle.putString("forum", forumTitle);

                    mNavController.navigate(R.id.action_navigation_forum_to_navigation_discussion, bundle);
                }
            });
        }

        private void updateFollowEffect() {
            mBtnFollow.setText(R.string.title_discussion_unfollow);

            int color = ContextCompat.getColor(itemView.getContext(), R.color.primary);
            mBtnFollow.setTextColor(color);

            mBtnFollow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_notifications_active_24dp, 0, 0, 0);

        }

        private void updateUnFollowEffect() {
            mBtnFollow.setText(R.string.title_discussion_follow);

            int color = ContextCompat.getColor(itemView.getContext(), R.color.primaryDark);
            mBtnFollow.setTextColor(color);

            mBtnFollow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_alert_24dp, 0, 0, 0);
        }

        protected abstract IStateLiveData<InterestedDiscussion> onFollow(int discussionId);
        protected abstract IStateLiveData<String> onUnFollow(int discussionId);
    }
}
