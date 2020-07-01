package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;
import java.util.logging.Handler;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.msg.AdminMessageHolder;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.msg.MessageHolder;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.msg.ReceiveMessageHolder;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.msg.SendMessageHolder;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.Message_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.util.StringUtils;
import vnu.uet.mobilecourse.assistant.view.component.ChatBox;
import vnu.uet.mobilecourse.assistant.view.component.MyClickableSpan;
import vnu.uet.mobilecourse.assistant.viewmodel.time.TimeLiveData;

public class MessageAdapter extends RecyclerView.Adapter<MessageHolder> {

    private static final String STUDENT_ID = User.getInstance().getStudentId();

    private static final int MSG_RECEIVE_TYPE = 0;
    private static final int MSG_SEND_TYPE = 1;
    private static final int MSG_ADMIN_TYPE = 2;

    private Fragment mOwner;
    private List<Message_GroupChatSubCol> mMessages;
    private NavController mNavController;

//    private TimeLiveData mTimer;

    public MessageAdapter(List<Message_GroupChatSubCol> messages, Fragment owner) {
        this.mMessages = messages;
        this.mOwner = owner;
//        this.mTimer = new TimeLiveData();
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MessageHolder holder;

        if (viewType == MSG_RECEIVE_TYPE) {
            holder = generateReceiveMessageViewHolder(parent);
        } else if (viewType == MSG_SEND_TYPE) {
            holder = generateSendMessageViewHolder(parent);
        } else {
            holder = generateAdminMessageViewHolder(parent);
        }

        Activity activity = mOwner.getActivity();

        if (activity != null) {
            mNavController = Navigation
                    .findNavController(activity, R.id.nav_host_fragment);
        }

        return holder;
    }

    private MessageHolder generateReceiveMessageViewHolder(@NonNull ViewGroup parent) {
        View view = mOwner.getLayoutInflater()
                .inflate(R.layout.layout_receive_message_item, parent, false);

        return new ReceiveMessageHolder(view, mOwner.getViewLifecycleOwner());
    }

    private MessageHolder generateSendMessageViewHolder(@NonNull ViewGroup parent) {
        View view =  mOwner.getLayoutInflater()
                .inflate(R.layout.layout_send_message_item, parent, false);

        return new SendMessageHolder(view);
    }

    private MessageHolder generateAdminMessageViewHolder(@NonNull ViewGroup parent) {
        View view =  mOwner.getLayoutInflater()
                .inflate(R.layout.layout_admin_message_item, parent, false);

        return new AdminMessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        Message_GroupChatSubCol message = mMessages.get(position);

        int visibilityType = MessageHolder.SHOW_FULL;

        if (position > 0) {
            int prevPosition = position - 1;
            Message_GroupChatSubCol prevMessage = mMessages.get(prevPosition);

            // under 1 minute
            if (message.getTimestamp() - prevMessage.getTimestamp() < 60) {
                if (prevMessage.getFromId().equals(message.getFromId())) {
                    visibilityType = MessageHolder.HIDE_INFO_N_TIME;
                } else visibilityType = MessageHolder.HIDE_TIME;
            }
        }

        holder.bind(message, visibilityType, mNavController);

//        mTimer.observe(mOwner.getViewLifecycleOwner(), new Observer<Long>() {
//            @Override
//            public void onChanged(Long aLong) {
//                String time = DateTimeUtils.generateViewText(message.getTimestamp());
//                holder.setTime(time);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message_GroupChatSubCol message = mMessages.get(position);

        String fromId = message.getFromId();

        if (STUDENT_ID.equals(fromId)) {
            return MSG_SEND_TYPE;
        } else if (fromId.equals("admin")) {
            return MSG_ADMIN_TYPE;
        } else
            return MSG_RECEIVE_TYPE;
    }

//    static class SendMessageViewHolder extends MessageViewHolder {
//
//        public SendMessageViewHolder(@NonNull View itemView) {
//            super(itemView);
//        }
//
//    }
//
//    static abstract class ReceiveMessageViewHolder extends MessageViewHolder {
//
//        private CircleImageView mCivAvatar;
//
//        public ReceiveMessageViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            mCivAvatar = itemView.findViewById(R.id.civAvatar);
//            mCivAvatar.setVisibility(View.VISIBLE);
//            mCivAvatar.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onAvatarClick();
//                }
//            });
//        }
//
//        @Override
//        protected void bind(Message_GroupChatSubCol message, boolean samePrev) {
//            super.bind(message, samePrev);
//
//            if (samePrev) mCivAvatar.setVisibility(View.INVISIBLE);
//        }
//
//        protected abstract void onAvatarClick();
//    }
//
//    abstract static class MessageViewHolder extends RecyclerView.ViewHolder {
//
//        TextView mTvTime;
//        TextView mTvMessage;
//
//        protected MessageViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            mTvTime = itemView.findViewById(R.id.tvTime);
//
//            mTvMessage = itemView.findViewById(R.id.tvMessage);
//            mTvMessage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mTvTime.getVisibility() == View.GONE) {
//                        mTvTime.setVisibility(View.VISIBLE);
//                    } else {
//                        mTvTime.setVisibility(View.GONE);
//                    }
//                }
//            });
//
//            itemView.setVisibility(View.INVISIBLE);
//        }
//
//        private SpannableStringBuilder insertClickable(SpannableStringBuilder span) {
//            StyleSpan[] styleSpans = span.getSpans(0, span.length(), StyleSpan.class);
//
//            for (StyleSpan styleSpan : styleSpans) {
//                if (styleSpan.getStyle() == Typeface.BOLD) {
//
//                    int start = span.getSpanStart(styleSpan);
//                    int end = span.getSpanEnd(styleSpan);
//
//                    CharSequence mention = span.subSequence(start, end);
//                    Log.d("SPANNNNN", "mention: " + mention);
//
//                    ClickableSpan clickableSpan = new MyClickableSpan() {
//                        @Override
//                        public void onClick(@NonNull View widget) {
//                            Toast.makeText(itemView.getContext(), mention, Toast.LENGTH_SHORT).show();
//                        }
//                    };
//
//                    span.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                }
//            }
//
//            return span;
//        }
//
//        protected void bind(Message_GroupChatSubCol message, boolean samePrev) {
////            itemView.setVisibility(View.INVISIBLE);
//
//            String rawContent = message.getContent();
//            SpannableStringBuilder formatContent = StringUtils.convertHtml(rawContent);
//            formatContent = insertClickable(formatContent);
//            mTvMessage.setText(formatContent);
//            mTvMessage.setMovementMethod(LinkMovementMethod.getInstance());
//            itemView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    int paddingEnd = itemView.getPaddingEnd();
//                    int paddingStart = itemView.getPaddingStart();
//                    int maxWidth = itemView.getWidth() - (paddingEnd + paddingStart);
//
//                    Log.d(getClass().getSimpleName(), "bind: " + maxWidth);
//
//                    if (mTvMessage instanceof ChatBox) {
//                        ((ChatBox) mTvMessage).reduce(maxWidth);
//                    }
//
//                    itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//
//                    showView();
//                }
//            });
//        }
//
//        private void showView() {
//            mTvMessage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    itemView.setVisibility(View.VISIBLE);
//                    mTvMessage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                }
//            });
//        }
//    }
}
