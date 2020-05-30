package vnu.uet.mobilecourse.assistant.adapter.viewholder;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.IStudent;

public class StudentViewHolder extends RecyclerView.ViewHolder {

    private CircleImageView mCivAvatar;
    private TextView mTvName;
    private TextView mTvId;
    private ImageButton mBtnChat;

    private View mView;

    public StudentViewHolder(@NonNull View view) {
        super(view);

        mCivAvatar = view.findViewById(R.id.civAvatar);
        mTvName = view.findViewById(R.id.tvName);
        mTvId = view.findViewById(R.id.tvId);
        mBtnChat = view.findViewById(R.id.btnChat);

        mView = view;
    }

    public void bind(IStudent student) {
        mTvName.setText(student.getName());
        mTvId.setText(student.getCode());

        if (!student.isActive()) {
            mBtnChat.setImageResource(R.drawable.ic_warning_24dp);
            mBtnChat.setBackground(null);
            mBtnChat.setClickable(false);
            mBtnChat.setEnabled(false);
        } else {
            mBtnChat.setImageResource(R.drawable.ic_chat_24dp);
            mBtnChat.setBackgroundResource(R.drawable.primary_button_background);
            mBtnChat.setClickable(true);
            mBtnChat.setEnabled(true);
        }
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mView.setOnClickListener(listener);
    }
}
