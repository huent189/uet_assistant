package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.Message_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.util.StringUtils;
import vnu.uet.mobilecourse.assistant.view.component.ChatBox;
import vnu.uet.mobilecourse.assistant.viewmodel.time.TimeLiveData;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private static final String STUDENT_ID = User.getInstance().getStudentId();

    private static final int MSG_RECEIVE_TYPE = 0;
    private static final int MSG_SEND_TYPE = 1;

    private Fragment mOwner;
    private List<Message_GroupChatSubCol> mMessages;
    private NavController mNavController;

    private TimeLiveData mTimer;

    public MessageAdapter(List<Message_GroupChatSubCol> messages, Fragment owner) {
        this.mMessages = messages;
        this.mOwner = owner;
        this.mTimer = new TimeLiveData();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MessageViewHolder holder;

        if (viewType == MSG_RECEIVE_TYPE) {
            holder = generateReceiveMessageViewHolder(parent);
        } else {
            holder = generateSendMessageViewHolder(parent);
        }

        Activity activity = mOwner.getActivity();

        if (activity != null) {
            mNavController = Navigation
                    .findNavController(activity, R.id.nav_host_fragment);
        }

        return holder;
    }

    private MessageViewHolder generateReceiveMessageViewHolder(@NonNull ViewGroup parent) {
        View view = mOwner.getLayoutInflater()
                .inflate(R.layout.layout_receive_message_item, parent, false);

        return new ReceiveMessageViewHolder(view) {

            @Override
            protected void onAvatarClick() {
                mNavController.navigateUp();
            }

        };
    }

    private MessageViewHolder generateSendMessageViewHolder(@NonNull ViewGroup parent) {
        View view =  mOwner.getLayoutInflater()
                .inflate(R.layout.layout_send_message_item, parent, false);

        return new SendMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message_GroupChatSubCol message = mMessages.get(position);

        boolean samePrev = false;

//        if (position > 0) {
//            int prevPosition = position - 1;
//            samePrev = getItemViewType(position) == getItemViewType(prevPosition);
//        }

//        ViewGroup.LayoutParams paramsReset = holder.mTvMessage.getLayoutParams();
//        paramsReset.width = ViewGroup.LayoutParams.WRAP_CONTENT;
//        holder.mTvMessage.setLayoutParams(paramsReset);

        holder.bind(message, samePrev);

        mTimer.observe(mOwner.getViewLifecycleOwner(), new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                String time = DateTimeUtils.generateViewText(message.getTimestamp());
                holder.mTvTime.setText(time);
            }
        });
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
        } else {
            return MSG_RECEIVE_TYPE;
        }
    }

    static class SendMessageViewHolder extends MessageViewHolder {

        public SendMessageViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }

    static abstract class ReceiveMessageViewHolder extends MessageViewHolder {

        private CircleImageView mCivAvatar;

        public ReceiveMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            mCivAvatar = itemView.findViewById(R.id.civAvatar);
            mCivAvatar.setVisibility(View.VISIBLE);
            mCivAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAvatarClick();
                }
            });
        }

        @Override
        protected void bind(Message_GroupChatSubCol message, boolean samePrev) {
            super.bind(message, samePrev);

            if (samePrev) mCivAvatar.setVisibility(View.INVISIBLE);
        }

        protected abstract void onAvatarClick();
    }

    abstract static class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView mTvTime;
        TextView mTvMessage;

        protected MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            mTvTime = itemView.findViewById(R.id.tvTime);

            mTvMessage = itemView.findViewById(R.id.tvMessage);
            mTvMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTvTime.getVisibility() == View.GONE) {
                        mTvTime.setVisibility(View.VISIBLE);
                    } else {
                        mTvTime.setVisibility(View.GONE);
                    }
                }
            });
            mTvMessage.setVisibility(View.INVISIBLE);
        }

        protected void bind(Message_GroupChatSubCol message, boolean samePrev) {
            mTvMessage.setVisibility(View.INVISIBLE);
            mTvMessage.setText(message.getContent());

            itemView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int paddingEnd = itemView.getPaddingEnd();
                    int paddingStart = itemView.getPaddingStart();
                    int maxWidth = itemView.getWidth() - (paddingEnd + paddingStart);

                    Log.d(getClass().getSimpleName(), "bind: " + maxWidth);

//                    String content = message.getContent();
//
//                    String[] sections = content.split(" ");
//                    String formattedMessage = StringUtils.splitTextToFitWidth(sections, mTvMessage.getPaint(), maxWidth);
//                    mTvMessage.setText(formattedMessage);

                    if (mTvMessage instanceof ChatBox) {
                        ((ChatBox) mTvMessage).reduce(maxWidth);
                    }

                    mTvMessage.setVisibility(View.VISIBLE);

                    itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });


        }
    }
}
