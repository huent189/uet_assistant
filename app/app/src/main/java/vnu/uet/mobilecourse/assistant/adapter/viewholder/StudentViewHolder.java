package vnu.uet.mobilecourse.assistant.adapter.viewholder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.IStudent;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.util.AvatarLoader;
import vnu.uet.mobilecourse.assistant.view.component.AvatarView;

public abstract class StudentViewHolder extends RecyclerView.ViewHolder {

    private AvatarView mAvatarView;
    private TextView mTvName;
    private TextView mTvId;
    private ImageButton mBtnChat;

    private static final String USER_ID = User.getInstance().getStudentId();

    public StudentViewHolder(@NonNull View view) {
        super(view);

        mAvatarView = view.findViewById(R.id.avatarView);
        mTvName = view.findViewById(R.id.tvName);
        mTvId = view.findViewById(R.id.tvId);
        mBtnChat = view.findViewById(R.id.btnChat);
    }

    public void bind(IStudent student, LifecycleOwner lifecycleOwner) {
        mTvName.setText(student.getName());
        mTvId.setText(student.getCode());
        mBtnChat.setVisibility(View.VISIBLE);

        if (!student.isActive()) {
            mBtnChat.setImageResource(R.drawable.ic_warning_24dp);
            mBtnChat.setBackground(null);
            mBtnChat.setClickable(false);
            mBtnChat.setEnabled(false);
            mAvatarView.setDefault();
        } else {
            mAvatarView.setLifecycleOwner(lifecycleOwner);
            mAvatarView.loadUser(student.getCode());

            if (student.getCode().equals(USER_ID)) {
                String name = student.getName() + " (tôi)";
                mTvName.setText(name);
                mBtnChat.setVisibility(View.GONE);
            } else {
                mBtnChat.setImageResource(R.drawable.ic_chat_24dp);
                mBtnChat.setBackgroundResource(R.drawable.primary_button_background);
                mBtnChat.setClickable(true);
                mBtnChat.setEnabled(true);

                mBtnChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onChatClick(student);
                    }
                });
            }
        }
    }

    protected abstract void onChatClick(IStudent student);

    public void setOnClickListener(View.OnClickListener listener) {
        itemView.setOnClickListener(listener);
    }
}
