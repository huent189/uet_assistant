package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
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

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private static final int MSG_RECEIVE_TYPE = 0;
    private static final int MSG_SEND_TYPE = 1;

    private Fragment owner;
    private List<String> messages;
    private NavController navController;

    public MessageAdapter(List<String> messages, Fragment owner) {
        this.messages = messages;
        this.owner = owner;
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

        Activity activity = owner.getActivity();

        if (activity != null)
            navController = Navigation.findNavController(activity, R.id.nav_host_fragment);

        return holder;
    }

    private MessageViewHolder generateReceiveMessageViewHolder(@NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = owner.getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.layout_receive_message_item, parent, false);

        return new ReceiveMessageViewHolder(view);
    }

    private MessageViewHolder generateSendMessageViewHolder(@NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = owner.getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.layout_send_message_item, parent, false);

        return new SendMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        String message = messages.get(position);

        holder.tvMessage.setText(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    public class SendMessageViewHolder extends MessageViewHolder {

        public SendMessageViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class ReceiveMessageViewHolder extends MessageViewHolder {
        private CircleImageView civAvatar;

        public ReceiveMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            civAvatar = itemView.findViewById(R.id.civAvatar);
        }
    }

    public abstract class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTime;

        private TextView tvMessage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTime = itemView.findViewById(R.id.tvTime);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
    }


}
