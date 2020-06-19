package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat_UserSubCol;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;

public class ChatGroupAdapter extends RecyclerView.Adapter<ChatGroupAdapter.ChatViewHolder> {

    private List<GroupChat_UserSubCol> mChats;
    private NavController mNavController;
    private Fragment mOwner;

    public ChatGroupAdapter(List<GroupChat_UserSubCol> chats, Fragment owner) {
        this.mChats = chats;
        this.mOwner = owner;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mOwner.getLayoutInflater()
                .inflate(R.layout.layout_group_chat_item, parent, false);

        Activity activity = mOwner.getActivity();

        if (activity != null) {
            mNavController = Navigation
                    .findNavController(activity, R.id.nav_host_fragment);
        }

        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        final GroupChat_UserSubCol current = mChats.get(position);
        holder.bind(current, mNavController);
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView mCivAvatar;
        private TextView mTvChatGroupTitle;
        private TextView mTvLastMessage;
        private TextView mTvLastMessageTime;
        private ImageView mIvStatus;

        ChatViewHolder(@NonNull View view) {
            super(view);

            mCivAvatar = view.findViewById(R.id.civAvatar);
            mTvChatGroupTitle = view.findViewById(R.id.tvChatGroupTitle);
            mTvLastMessage = view.findViewById(R.id.tvLastMessage);
            mTvLastMessageTime = view.findViewById(R.id.tvLastMessageTime);
            mIvStatus = view.findViewById(R.id.ivStatus);
        }

        void bind(GroupChat_UserSubCol chat, NavController navController) {
            mTvChatGroupTitle.setText(chat.getName());
            mTvLastMessage.setText(chat.getLastMessage());

            Date lastMessageTime = DateTimeUtils.fromSecond(chat.getLastMessageTime());
            String lastMessageTimeInStr = DateTimeUtils.TIME_12H_FORMAT.format(lastMessageTime);
            mTvLastMessageTime.setText(lastMessageTimeInStr);

            mIvStatus.setVisibility(chat.isSeen() ? View.GONE : View.VISIBLE);

            itemView.setOnClickListener(v ->
                    navController.navigate(
                            R.id.action_navigation_explore_course_to_navigation_friend_profile
                    )
            );
        }
    }

}
