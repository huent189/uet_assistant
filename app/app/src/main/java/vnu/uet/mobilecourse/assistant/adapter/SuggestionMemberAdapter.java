package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.IStudent;
import vnu.uet.mobilecourse.assistant.util.AvatarLoader;
import vnu.uet.mobilecourse.assistant.view.chat.AddMemberFragment;
import vnu.uet.mobilecourse.assistant.view.component.AvatarView;

public class SuggestionMemberAdapter extends RecyclerView.Adapter<SuggestionMemberAdapter.ViewHolder> {

    private List<IStudent> mStudents;
    private NavController mNavController;
    private AddMemberFragment mOwner;
    private OnCheckChangeListener mOnCheckChangeListener;

    public SuggestionMemberAdapter(List<IStudent> students, AddMemberFragment owner, OnCheckChangeListener onCheckChangeListener) {
        this.mStudents = students;
        this.mOwner = owner;
        this.mOnCheckChangeListener = onCheckChangeListener;
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

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final IStudent current = mStudents.get(position);
        holder.bind(current, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mOnCheckChangeListener.onCheckedChanged(current, isChecked);
            }
        }, mOwner);
    }

    public interface OnCheckChangeListener {
        void onCheckedChanged(IStudent student, boolean isChecked);
    }

    @Override
    public int getItemCount() {
        return mStudents.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private AvatarView mAvatarView;
        private TextView mTvName;
        private TextView mTvId;
        private CheckBox mCheckbox;

        ViewHolder(@NonNull View view) {
            super(view);

            mAvatarView = view.findViewById(R.id.avatarView);
            mTvName = view.findViewById(R.id.tvName);
            mTvId = view.findViewById(R.id.tvId);
            mCheckbox = view.findViewById(R.id.checkbox);
        }

        void bind(IStudent student, CompoundButton.OnCheckedChangeListener listener, AddMemberFragment fragment) {
            mTvName.setText(student.getName());
            mTvId.setText(student.getCode());

//            new AvatarLoader(itemView.getContext(), fragment.getViewLifecycleOwner())
//                    .loadUser(student.getCode(), mCivAvatar);
            mAvatarView.setLifecycleOwner(fragment.getViewLifecycleOwner());
            mAvatarView.loadUser(student.getCode());

            fragment.getViewModel().isSelected(student).observe(fragment.getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    mCheckbox.setChecked(aBoolean);
                }
            });

            mCheckbox.setOnCheckedChangeListener(listener);
        }
    }
}
