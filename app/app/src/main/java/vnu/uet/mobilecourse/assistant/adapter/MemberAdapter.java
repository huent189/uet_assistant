package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.IStudent;
import vnu.uet.mobilecourse.assistant.util.StringConst;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {

    private List<IStudent> mMembers;
    private Fragment mOwner;
    private NavController mNavController;
    private OnClearListener mOnClearListener;

    public MemberAdapter(List<IStudent> member, Fragment owner, OnClearListener onClearListener) {
        this.mMembers = member;
        this.mOwner = owner;
        this.mOnClearListener = onClearListener;
    }

    public void setMembers(List<IStudent> mMembers) {
        this.mMembers = mMembers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mOwner.getLayoutInflater()
                .inflate(R.layout.layout_short_student_item, parent, false);

        ViewHolder holder = new ViewHolder(view);

        Activity activity = mOwner.getActivity();

        if (activity != null) {
            mNavController = Navigation
                    .findNavController(activity, R.id.nav_host_fragment);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final IStudent student = mMembers.get(position);
        holder.bind(student, new Runnable() {
            @Override
            public void run() {
                mOnClearListener.onClear(student);
                notifyDataSetChanged();
            }
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

        private CircleImageView mCivAvatar;
        private TextView mTvName;
        private ImageButton mBtnClear;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            mCivAvatar = itemView.findViewById(R.id.civAvatar);
            mTvName = itemView.findViewById(R.id.tvName);
            mBtnClear = itemView.findViewById(R.id.btnClear);
        }

        void bind(IStudent student, Runnable callback) {
            mTvName.setText(getSimpleName(student.getName()));

            mBtnClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.run();
                }
            });
        }

        private String getSimpleName(String fullName) {
            String simpleName;

            String[] items = fullName.split("\\s+");

            int length = items.length;
            if (length > 2) {
                simpleName = items[length - 2] + StringConst.SPACE_CHAR + items[length - 1];
            } else {
                simpleName = fullName;
            }

            return simpleName;
        }
    }
}
