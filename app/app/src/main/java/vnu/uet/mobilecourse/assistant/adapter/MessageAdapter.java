package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.firebase.Message_GroupChatSubCol;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private static final int MSG_RECEIVE_TYPE = 0;
    private static final int MSG_SEND_TYPE = 1;

    private Fragment mOwner;
    private List<Message_GroupChatSubCol> mMessages;
    private NavController mNavController;

    public MessageAdapter(List<Message_GroupChatSubCol> messages, Fragment owner) {
        this.mMessages = messages;
        this.mOwner = owner;
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

        return new ReceiveMessageViewHolder(view);
    }

    private MessageViewHolder generateSendMessageViewHolder(@NonNull ViewGroup parent) {
        View view =  mOwner.getLayoutInflater()
                .inflate(R.layout.layout_send_message_item, parent, false);

        return new SendMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message_GroupChatSubCol message = mMessages.get(position);

        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    static class SendMessageViewHolder extends MessageViewHolder {
        SendMessageViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        void bind(Message_GroupChatSubCol message) {

        }
    }

    static class ReceiveMessageViewHolder extends MessageViewHolder {

        private CircleImageView mCivAvatar;

        ReceiveMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            mCivAvatar = itemView.findViewById(R.id.civAvatar);
        }

        @Override
        void bind(Message_GroupChatSubCol message) {

        }
    }

    abstract static class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView mTvTime;
        TextView mTvMessage;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            mTvTime = itemView.findViewById(R.id.tvTime);
            mTvMessage = itemView.findViewById(R.id.tvMessage);
        }

        abstract void bind(Message_GroupChatSubCol message);
    }


}
