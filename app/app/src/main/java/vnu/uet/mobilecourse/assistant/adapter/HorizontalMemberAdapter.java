package vnu.uet.mobilecourse.assistant.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.IStudent;
import vnu.uet.mobilecourse.assistant.util.AvatarLoader;
import vnu.uet.mobilecourse.assistant.util.StringUtils;
import vnu.uet.mobilecourse.assistant.view.component.AvatarView;

public class HorizontalMemberAdapter extends RecyclerView.Adapter<HorizontalMemberAdapter.ViewHolder> {

    private List<IStudent> mMembers;
    private Fragment mOwner;
    private OnClearListener mOnClearListener;

    public HorizontalMemberAdapter(List<IStudent> member, Fragment owner, OnClearListener onClearListener) {
        this.mMembers = member;
        this.mOwner = owner;
        this.mOnClearListener = onClearListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mOwner.getLayoutInflater()
                .inflate(R.layout.layout_short_student_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final IStudent student = mMembers.get(position);
        holder.bind(student, mOwner.getViewLifecycleOwner(), () -> {
            mOnClearListener.onClear(student);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return mMembers.size();
    }

    public interface OnClearListener {
        void onClear(IStudent student);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private AvatarView mAvatarView;
        private TextView mTvName;
        private ImageButton mBtnClear;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            mAvatarView = itemView.findViewById(R.id.avatarView);
            mTvName = itemView.findViewById(R.id.tvName);
            mBtnClear = itemView.findViewById(R.id.btnClear);
        }

        void bind(IStudent student, LifecycleOwner lifecycleOwner, Runnable callback) {
            mTvName.setText(getSimpleName(student.getName()));
            mBtnClear.setOnClickListener(view -> callback.run());

            mAvatarView.setLifecycleOwner(lifecycleOwner);
            mAvatarView.loadUser(student.getCode());
        }

        private String getSimpleName(String fullName) {
            return StringUtils.getLastSegment(fullName, 2);
        }
    }
}
