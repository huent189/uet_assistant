package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.IStudent;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat_UserSubCol;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.view.chat.ChatFragment;

public class SuggestionMemberAdapter extends RecyclerView.Adapter<SuggestionMemberAdapter.ViewHolder> {

    private List<IStudent> mStudents;
    private NavController mNavController;
    private Fragment mOwner;
    private OnCheckChangeListener mOnCheckChangeListener;

    public SuggestionMemberAdapter(List<IStudent> students, Fragment owner, OnCheckChangeListener onCheckChangeListener) {
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
        });
    }

    public interface OnCheckChangeListener {
        void onCheckedChanged(IStudent student, boolean isChecked);
    }

    @Override
    public int getItemCount() {
        return mStudents.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView mCivAvatar;
        private TextView mTvName;
        private TextView mTvId;
        private CheckBox mCheckbox;

        ViewHolder(@NonNull View view) {
            super(view);

            mCivAvatar = view.findViewById(R.id.civAvatar);
            mTvName = view.findViewById(R.id.tvName);
            mTvId = view.findViewById(R.id.tvId);
            mCheckbox = view.findViewById(R.id.checkbox);
        }

        void bind(IStudent student, CompoundButton.OnCheckedChangeListener listener) {
            mTvName.setText(student.getName());
            mTvId.setText(student.getCode());
            mCheckbox.setOnCheckedChangeListener(listener);
        }
    }
}
