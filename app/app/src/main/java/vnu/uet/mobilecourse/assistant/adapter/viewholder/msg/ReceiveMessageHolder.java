package vnu.uet.mobilecourse.assistant.adapter.viewholder.msg;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.NavController;
import de.hdodenhof.circleimageview.CircleImageView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.firebase.Message_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.util.AvatarLoader;
import vnu.uet.mobilecourse.assistant.util.StringUtils;
import vnu.uet.mobilecourse.assistant.view.component.AvatarView;

public class ReceiveMessageHolder extends MessageHolder {

    private AvatarView mAvatarView;
    private TextView mTvName;
    private LifecycleOwner mLifecycleOwner;

    public ReceiveMessageHolder(@NonNull View itemView, LifecycleOwner lifecycleOwner) {
        super(itemView);

        mAvatarView = itemView.findViewById(R.id.avatarView);
        mTvName = itemView.findViewById(R.id.tvName);
        mAvatarView.setVisibility(View.VISIBLE);
        mLifecycleOwner = lifecycleOwner;
    }

    @Override
    public void bind(Message_GroupChatSubCol message, int visibilityType, NavController navController) {
        super.bind(message, visibilityType, navController);

        switch (visibilityType) {
            case SHOW_FULL:
            case HIDE_TIME:
                mAvatarView.setVisibility(View.VISIBLE);
                mTvName.setVisibility(View.VISIBLE);
                break;

            case HIDE_INFO_N_TIME:
                mAvatarView.setVisibility(View.INVISIBLE);
                mTvName.setVisibility(View.GONE);
                break;
        }

        String simpleName = StringUtils.getLastSegment(message.getFromName(), 2);
        mTvName.setText(simpleName);

        mAvatarView.setLifecycleOwner(mLifecycleOwner);
        mAvatarView.loadUser(message.getFromId());

        mAvatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("name", message.getFromName());
                bundle.putString("code", message.getFromId());
                bundle.putBoolean("active", true);
                bundle.putBoolean("fromChat", true);

                navController.navigate(R.id.action_navigation_chat_room_to_navigation_friend_profile, bundle);
            }
        });
    }
}
