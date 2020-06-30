package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.ISwipeToDeleteHolder;
import vnu.uet.mobilecourse.assistant.model.IStudent;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.util.AvatarLoader;
import vnu.uet.mobilecourse.assistant.util.DimensionUtils;
import vnu.uet.mobilecourse.assistant.view.component.AvatarView;
import vnu.uet.mobilecourse.assistant.view.profile.RoomProfileFragment;

public class VerticalMemberAdapter extends RecyclerView.Adapter<VerticalMemberAdapter.ViewHolder> {

    private List<? extends IStudent> mStudents;
    private NavController mNavController;
    private Fragment mOwner;

    public VerticalMemberAdapter(List<? extends IStudent> students, Fragment owner) {
        this.mStudents = students;
        this.mOwner = owner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mOwner.getLayoutInflater()
                .inflate(R.layout.layout_multi_choice_student_item, parent, false);

        Activity activity = mOwner.getActivity();

        if (activity != null) {
            mNavController = Navigation
                    .findNavController(activity, R.id.nav_host_fragment);
        }

        return new ViewHolder(view, mOwner);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final IStudent current = mStudents.get(position);
        holder.bind(current);
    }

    public IStudent getStudent(int position) {
        return mStudents.get(position);
    }

    @Override
    public int getItemCount() {
        return mStudents.size();
    }

    private static final String USER_ID = User.getInstance().getStudentId();

    static class ViewHolder extends RecyclerView.ViewHolder implements ISwipeToDeleteHolder {

        private AvatarView mAvatarView;
        private TextView mTvName;
        private TextView mTvId;
        private LifecycleOwner mLifecycleOwner;

        ViewHolder(@NonNull View view, Fragment owner) {
            super(view);

            mAvatarView = view.findViewById(R.id.avatarView);
            mTvName = view.findViewById(R.id.tvName);
            mTvId = view.findViewById(R.id.tvId);
            CheckBox checkBox = view.findViewById(R.id.checkbox);
            checkBox.setVisibility(View.GONE);

            if (owner instanceof RoomProfileFragment) {
                itemView.setBackgroundResource(R.drawable.ripple_highlight);
                final int paddingH = DimensionUtils.dpToPx(16, itemView.getContext());
                final int paddingV = DimensionUtils.dpToPx(12, itemView.getContext());
                itemView.setPaddingRelative(paddingH, paddingV, paddingH, paddingV);

                mTvName.setTextColor(Color.WHITE);
                mTvName.setTypeface(Typeface.create((Typeface) null, Typeface.NORMAL));
            }

            mLifecycleOwner = owner.getViewLifecycleOwner();
        }

        void bind(IStudent student) {
            mTvName.setText(student.getName());
            mTvId.setText(student.getCode());

            if (student.getCode().equals(USER_ID)) {
                String name = student.getName() + " (tôi)";
                mTvName.setText(name);
            }

            mAvatarView.setLifecycleOwner(mLifecycleOwner);
            mAvatarView.loadUser(student.getCode());
        }
    }
}
