package vnu.uet.mobilecourse.assistant.adapter.viewholder.msg;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.firebase.Message_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.util.StringConst;
import vnu.uet.mobilecourse.assistant.util.StringUtils;
import vnu.uet.mobilecourse.assistant.view.component.CardAttachment;
import vnu.uet.mobilecourse.assistant.view.component.ChatBox;
import vnu.uet.mobilecourse.assistant.view.component.CustomImageView;
import vnu.uet.mobilecourse.assistant.view.component.MyClickableSpan;

public abstract class MessageHolder extends RecyclerView.ViewHolder {

    private TextView mTvTime;
    private TextView mTvMessage;

    private CustomImageView mImageView;
    private CardAttachment mCardAttachment;

    protected MessageHolder(@NonNull View itemView) {
        super(itemView);

        mTvTime = itemView.findViewById(R.id.tvTime);
        mTvMessage = itemView.findViewById(R.id.tvMessage);
        mImageView = itemView.findViewById(R.id.customImageView);
        mCardAttachment = itemView.findViewById(R.id.cardAttachment);
    }

    private boolean checkAttachment(Message_GroupChatSubCol message) {
        String contentType = message.getContentType();
        if (contentType != null && contentType.startsWith("text/")) {
            mImageView.setVisibility(View.GONE);
            mCardAttachment.setVisibility(View.GONE);
            mTvMessage.setVisibility(View.VISIBLE);
            return false;
        } else if (contentType != null && contentType.startsWith("image/")) {
            String path = message.getAttachmentPath();
            mImageView.setImage(path);
            mImageView.setVisibility(View.VISIBLE);
            mCardAttachment.setVisibility(View.GONE);
            mTvMessage.setVisibility(View.GONE);
        } else {
            String path = message.getAttachmentPath();
            mCardAttachment.setAttachment(path);
            mImageView.setVisibility(View.GONE);
            mCardAttachment.setVisibility(View.VISIBLE);
            mTvMessage.setVisibility(View.GONE);
        }
        return true;
    }

    public void bind(Message_GroupChatSubCol message, int visibilityType, NavController navController) {
        switch (visibilityType) {
            case SHOW_FULL:
                mTvTime.setVisibility(View.VISIBLE);
                break;

            case HIDE_TIME:
            case HIDE_INFO_N_TIME:
                mTvTime.setVisibility(View.GONE);
                break;
        }

        String time = DateTimeUtils.generateViewText(message.getTimestamp());
        mTvTime.setText(time);

        boolean haveAttachment = checkAttachment(message);

        if (!haveAttachment) {
            String rawContent = message.getContent();
            SpannableStringBuilder formatContent = StringUtils.convertHtml(rawContent);

            insertClickable(formatContent, new OnMentionClickListener() {
                @Override
                public void onClick(String memberId) {
                    Bundle bundle = new Bundle();
                    bundle.putString("code", memberId);
                    bundle.putBoolean("active", true);
                    bundle.putBoolean("fromChat", true);

                    navController.navigate(R.id.action_navigation_chat_room_to_navigation_friend_profile, bundle);
                }
            });

            mTvMessage.setText(formatContent);
            mTvMessage.setMovementMethod(LinkMovementMethod.getInstance());

            itemView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int paddingEnd = itemView.getPaddingEnd();
                    int paddingStart = itemView.getPaddingStart();
                    int maxWidth = itemView.getWidth() - (paddingEnd + paddingStart);

                    Log.d(getClass().getSimpleName(), "bind: " + maxWidth);

                    if (mTvMessage instanceof ChatBox) {
                        ((ChatBox) mTvMessage).reduce(maxWidth);
                    }

                    itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    showView();
                }
            });
        }
    }

    private void insertClickable(SpannableStringBuilder ssb, OnMentionClickListener l) {
        StyleSpan[] styleSpans = ssb.getSpans(0, ssb.length(), StyleSpan.class);

        for (StyleSpan styleSpan : styleSpans) {
            if (styleSpan.getStyle() == Typeface.BOLD) {

                int start = ssb.getSpanStart(styleSpan);
                int end = ssb.getSpanEnd(styleSpan);

                CharSequence mention = ssb.subSequence(start, end);

                final String mentionId;
                if (mention.charAt(0) == StringConst.AT_SIGN) {
                    mentionId = mention
                            .subSequence(1, mention.length())
                            .toString();
                } else {
                    mentionId = mention.toString();
                }

                ClickableSpan clickableSpan = new MyClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        l.onClick(mentionId);
                    }
                };

                ssb.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private void showView() {
        mTvMessage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mTvMessage.setVisibility(View.VISIBLE);
                mTvMessage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    public interface OnMentionClickListener {
        void onClick(String memberId);
    }

    public static final int SHOW_FULL = 0;
    public static final int HIDE_INFO_N_TIME = 1;
    public static final int HIDE_TIME = 2;
}