package vnu.uet.mobilecourse.assistant.adapter.viewholder;

import android.view.View;
import android.widget.Button;
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
    private Button mBtnChat;

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
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mView.setOnClickListener(listener);
    }
}
