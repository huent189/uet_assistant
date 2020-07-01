package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import de.hdodenhof.circleimageview.CircleImageView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat_UserSubCol;
import vnu.uet.mobilecourse.assistant.util.AvatarLoader;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.util.FirebaseStructureId;
import vnu.uet.mobilecourse.assistant.util.StringUtils;
import vnu.uet.mobilecourse.assistant.view.GlideApp;
import vnu.uet.mobilecourse.assistant.view.chat.ChatFragment;
import vnu.uet.mobilecourse.assistant.view.component.AvatarView;

public class ChatGroupAdapter extends RecyclerView.Adapter<ChatGroupAdapter.ChatViewHolder> implements Filterable {

    private List<GroupChat_UserSubCol> mChats;
    private List<GroupChat_UserSubCol> mFullList;
    private NavController mNavController;
    private Fragment mOwner;
    private Filter mFilter;

    public ChatGroupAdapter(List<GroupChat_UserSubCol> chats, Fragment owner) {
        this.mChats = new ArrayList<>(chats);
        this.mFullList = chats;
        this.mOwner = owner;
        this.mFilter = new RoomFilter();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = mOwner.getLayoutInflater();

        View view;
        if (viewType == TYPE_SEEN) {
            view = inflater.inflate(R.layout.layout_group_chat_item, parent, false);
        } else {
            view = inflater.inflate(R.layout.layout_unseen_group_chat_item, parent, false);
        }

        Activity activity = mOwner.getActivity();

        if (activity != null) {
            mNavController = Navigation
                    .findNavController(activity, R.id.nav_host_fragment);
        }

        return new ChatViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return mChats.get(position).isSeen() ? TYPE_SEEN : TYPE_UNSEEN;
    }

    private static final int TYPE_SEEN = 0;
    private static final int TYPE_UNSEEN = 1;

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        final GroupChat_UserSubCol current = mChats.get(position);
        holder.bind(current, mNavController, mOwner.getClass().getSimpleName(), mOwner.getViewLifecycleOwner());
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public List<GroupChat_UserSubCol> getVisibleRooms() {
        return mChats;
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        private AvatarView mAvatarView;
        private TextView mTvChatGroupTitle;
        private TextView mTvLastMessage;
        private TextView mTvLastMessageTime;
        private ImageView mIvStatus;
        private View mLayoutTime;
        private View mLayoutContainer;
        private View mIvNew;

        ChatViewHolder(@NonNull View view) {
            super(view);

            mAvatarView = view.findViewById(R.id.avatarView);
            mTvChatGroupTitle = view.findViewById(R.id.tvChatGroupTitle);
            mTvLastMessage = view.findViewById(R.id.tvLastMessage);
            mTvLastMessageTime = view.findViewById(R.id.tvLastMessageTime);
            mIvStatus = view.findViewById(R.id.ivStatus);
            mLayoutTime = view.findViewById(R.id.layoutTime);
            mLayoutContainer = view.findViewById(R.id.layoutContainer);
//            mIvNew = view.findViewById(R.id.ivNew);
        }

        void bind(GroupChat_UserSubCol chat, NavController navController, String ownerName, LifecycleOwner lifecycleOwner) {
            mTvChatGroupTitle.setText(chat.getName());

            Date lastMessageTime = DateTimeUtils.fromSecond(chat.getLastMessageTime());
            String lastMessageTimeInStr = DateTimeUtils.TIME_12H_FORMAT.format(lastMessageTime);
            mTvLastMessageTime.setText(lastMessageTimeInStr);

            mAvatarView.setLifecycleOwner(lifecycleOwner);

            if (chat.getType().equals(GroupChat.DIRECT)) {
                String mateId = FirebaseStructureId.getMateId(chat.getId());
                mAvatarView.loadUser(mateId);
            } else {
                mAvatarView.loadRoom(chat.getId());
            }

            mIvStatus.setImageDrawable(null);

            if (chat.isSeen()) {
//                GlideApp.with(itemView.getContext())
//                        .load(R.drawable.ic_check_circle_24dp)
//                        .into(mIvStatus);
//                Drawable drawable = itemView.getContext().getApplicationContext()
//                        .getResources().getDrawable(R.drawable.ic_check_circle_24dp, null);
////                mIvStatus.setImageResource(R.drawable.ic_check_circle_24dp);
//                mIvStatus.setImageDrawable(drawable);
//                mIvStatus.setScaleX(1.25f);
//                mIvStatus.setScaleY(1.25f);
//                mIvStatus.setVisibility(View.VISIBLE);
//                mIvNew.setVisibility(View.GONE);

//                mTvLastMessage.setTypeface(null, Typeface.NORMAL);

//                mLayoutNonSeen.setVisibility(View.GONE);
            } else {
//                GlideApp.with(itemView.getContext())
//                        .load(R.drawable.circle)
//                        .into(mIvStatus);
//                Drawable drawable = itemView.getContext().getApplicationContext()
//                        .getResources().getDrawable(R.drawable.circle, null);
//                mIvStatus.setImageDrawable(drawable);
//
////                mIvStatus.setImageResource(R.drawable.circle);
//                mIvStatus.setScaleX(1f);
//                mIvStatus.setScaleY(1f);
//                mIvStatus.setVisibility(View.GONE);
//                mIvNew.setVisibility(View.VISIBLE);

//                mTvLastMessage.setTypeface(null, Typeface.BOLD);

//                mLayoutNonSeen.setVisibility(View.VISIBLE);
            }

            mLayoutTime.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int maxWidth = mLayoutContainer.getWidth() - mLayoutTime.getWidth();

                    mTvLastMessage.setMaxWidth(maxWidth);

                    if (chat.getLastMessage() != null) {
                        SpannableStringBuilder format = StringUtils.convertHtml(chat.getLastMessage());
                        mTvLastMessage.setText(format.toString());
                    }

                    mLayoutTime.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });

            itemView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("title", chat.getName());
                bundle.putString("type", chat.getType());
                bundle.putString("roomId", chat.getId());

                int actionId;
                if (ownerName.equals(ChatFragment.class.getSimpleName())) {
                    actionId = R.id.action_navigation_chat_to_navigation_chat_room;
                } else {
                    actionId = R.id.action_navigation_search_student_to_navigation_chat_room;
                }

                navController.navigate(actionId, bundle);
            });
        }
    }

    public class RoomFilter extends MyFilter<GroupChat_UserSubCol> {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<GroupChat_UserSubCol> filteredList;

            if (constraint == null || constraint.length() == 0) {
                filteredList = new ArrayList<>(mFullList);
            } else {
                final String filterPattern = constraint.toString().trim();

                filteredList = mFullList.stream()
                        .filter(room -> room.getName().contains(filterPattern)
                                || (room.getType().equals(GroupChat.DIRECT)
                                    && room.getId().contains(filterPattern))
                        ).collect(Collectors.toList());
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mChats = getListFromResults(results);
            notifyDataSetChanged();
        }
    }
}
