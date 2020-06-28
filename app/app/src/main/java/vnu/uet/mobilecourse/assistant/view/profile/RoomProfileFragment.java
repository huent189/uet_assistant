package vnu.uet.mobilecourse.assistant.view.profile;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.VerticalMemberAdapter;
import vnu.uet.mobilecourse.assistant.alarm.scheduler.TodoScheduler;
import vnu.uet.mobilecourse.assistant.model.IStudent;
import vnu.uet.mobilecourse.assistant.model.event.IEvent;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.view.component.SwipeToDeleteCallback;
import vnu.uet.mobilecourse.assistant.viewmodel.RoomProfileViewModel;
import vnu.uet.mobilecourse.assistant.adapter.ClassMateAdapter;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class RoomProfileFragment extends Fragment {

    private RoomProfileViewModel mViewModel;
    private NavController mNavController;
    private FragmentActivity mActivity;

    private VerticalMemberAdapter mMemberAdapter;
    private RecyclerView mRvMembers;

    private GroupChat mRoom;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(RoomProfileViewModel.class);

        mActivity = getActivity();
        if (mActivity != null) {
            mNavController = Navigation.findNavController(mActivity, R.id.nav_host_fragment);
        }

        View root = inflater.inflate(R.layout.fragment_room_profile, container, false);

        View layoutRename = root.findViewById(R.id.layout_rename);
        View layoutChangeAvatar = root.findViewById(R.id.layout_change_avatar);
        View layoutAddMember = root.findViewById(R.id.layout_add_member);

        if (getArguments() != null) {
            mRoom = getArguments().getParcelable("room");

            if (mRoom != null) {
                TextView tvTitle = root.findViewById(R.id.tvTitle);
                tvTitle.setText(mRoom.getName());

                mRvMembers = root.findViewById(R.id.rvMembers);

                String roomId = mRoom.getId();

                mViewModel.getRoomInfo(roomId).observe(getViewLifecycleOwner(), new Observer<StateModel<GroupChat>>() {
                    @Override
                    public void onChanged(StateModel<GroupChat> stateModel) {
                        switch (stateModel.getStatus()) {
                            case SUCCESS:
                                mRoom = stateModel.getData();

                                tvTitle.setText(mRoom.getName());

                                mMemberAdapter = new VerticalMemberAdapter(mRoom.getMembers(), RoomProfileFragment.this);
                                mRvMembers.setAdapter(mMemberAdapter);

                                break;
                        }
                    }
                });

            }
        }

        layoutRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        layoutChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        layoutAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("room", mRoom);

                mNavController.navigate(R.id.action_navigation_room_profile_to_navigation_add_member, bundle);
            }
        });

        enableSwipeToDelete();

        return root;
    }

    private void enableSwipeToDelete() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final IStudent item = mMemberAdapter.getStudent(position);

                mViewModel.removeMember(mRoom.getId(), item.getCode())
                        .observe(getViewLifecycleOwner(), stateModel -> {
                            if (stateModel.getStatus() == StateStatus.ERROR) {
                                final String FAILURE_MSG = "Xóa thành viên thất bại";
                                Toast.makeText(mActivity, FAILURE_MSG, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(mRvMembers);
    }
}
