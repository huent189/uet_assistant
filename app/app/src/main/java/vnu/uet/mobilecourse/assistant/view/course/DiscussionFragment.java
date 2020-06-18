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
import vnu.uet.mobilecourse.assistant.viewmodel.DiscussionViewModel;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.forum.Discussion;
import vnu.uet.mobilecourse.assistant.model.forum.Post;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.util.StringUtils;
import vnu.uet.mobilecourse.assistant.view.component.RepliesView;

import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DiscussionFragment extends Fragment {

    private DiscussionViewModel mViewModel;
    private FragmentActivity mActivity;

    public static DiscussionFragment newInstance() {
        return new DiscussionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(DiscussionViewModel.class);

        mActivity = getActivity();

        View root = inflater.inflate(R.layout.fragment_discussion, container, false);

        RepliesView repliesView = root.findViewById(R.id.repliesView);

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

                TextView tvContent = root.findViewById(R.id.tvContent);

                ImageView ivStarred = root.findViewById(R.id.ivStarred);
                if (discussion.isStarred()) {
                    ivStarred.setImageResource(R.drawable.ic_star_border_24dp);
                } else {
                    ivStarred.setImageResource(R.drawable.ic_star_border_24dp);
                }

                TextView tvReplies = root.findViewById(R.id.tvReplies);
                tvReplies.setText(String.valueOf(discussion.getNumberReplies()));

                mViewModel.getDiscussionDetail(discussion.getId()).observe(getViewLifecycleOwner(), new Observer<Post>() {
                    @Override
                    public void onChanged(Post post) {
                        if (post != null) {
                            SpannableStringBuilder rootContent = StringUtils.convertHtml(post.getMessage());
                            tvContent.setText(rootContent);

                            repliesView.setRootPost(post);

                            root.setBackgroundResource(R.color.backgroundLight);
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
