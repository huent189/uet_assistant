package vnu.uet.mobilecourse.assistant.view.component;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.InternalResourceAdapter;
import vnu.uet.mobilecourse.assistant.model.forum.Post;
import vnu.uet.mobilecourse.assistant.model.material.InternalFile;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.util.StringUtils;

public class RepliesView extends LinearLayout {

    private Post mRootPost;
    private Context mContext;
    private ViewGroup mRootView;
    private LayoutInflater mInflater;

    public RepliesView(Context context) {
        super(context);
        mContext = context;
        initializeLayout();
    }

    public RepliesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initializeLayout();
    }

    public RepliesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initializeLayout();
    }

    public RepliesView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        initializeLayout();
    }

    private void initializeLayout() {
        setOrientation(VERTICAL);

        mInflater = LayoutInflater.from(mContext);

        if (mInflater != null) {
            mRootView = (ViewGroup) mInflater.inflate(R.layout.layout_replies_panel, this);
        }
    }

    public void setRootPost(Post post) {
        mRootPost = post;
        mRootView.removeAllViews();

        for (Post reply : post.getReplies()) {
            ViewGroup firstReply = setupReplyView(mRootView, reply);

            firstReply.setPaddingRelative(toPixels(16), toPixels(16), toPixels(16), toPixels(8));

            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(0, toPixels(8), 0, 0);
            firstReply.setLayoutParams(params);
            firstReply.setBackgroundResource(R.color.background);
        }
    }

    private int toPixels(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale);
    }

    private ViewGroup setupReplyView(ViewGroup parent, Post post) {
        ViewGroup replayView = (ViewGroup) mInflater.inflate(R.layout.layout_post_item, parent, false);

        TextView tvAuthorName = replayView.findViewById(R.id.tvAuthorName);
        tvAuthorName.setText(post.getAuthorName());

        TextView tvCreatedTime = replayView.findViewById(R.id.tvCreatedTime);
        String createdTime = DateTimeUtils.generateViewText(post.getTimeCreated());
        tvCreatedTime.setText(createdTime);

        TextView tvMessage = replayView.findViewById(R.id.tvMessage);
        SpannableStringBuilder message = StringUtils.convertHtml(post.getMessage());
        tvMessage.setText(message);

        RecyclerView rvAttachments = replayView.findViewById(R.id.rvAttachments);
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false);

        rvAttachments.setLayoutManager(layoutManager);
        List<InternalFile> files = post.getAttachments();

        if (files != null && !files.isEmpty()) {
            InternalResourceAdapter adapter = new InternalResourceAdapter(files, mContext);
            rvAttachments.setAdapter(adapter);

            rvAttachments.setVisibility(View.VISIBLE);
        } else {
            rvAttachments.setVisibility(View.GONE);
        }

        parent.addView(replayView);

        for (Post reply : post.getReplies()) {
            setupReplyView(replayView, reply);
        }

        return replayView;
    }
}
