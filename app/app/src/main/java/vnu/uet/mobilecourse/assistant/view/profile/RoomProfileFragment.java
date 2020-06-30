package vnu.uet.mobilecourse.assistant.view.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.VerticalMemberAdapter;
import vnu.uet.mobilecourse.assistant.model.IStudent;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.util.AvatarLoader;
import vnu.uet.mobilecourse.assistant.util.FileUtils;
import vnu.uet.mobilecourse.assistant.view.chat.RenameDialog;
import vnu.uet.mobilecourse.assistant.view.component.SwipeToDeleteCallback;
import vnu.uet.mobilecourse.assistant.viewmodel.RoomProfileViewModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

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

                mViewModel.getRoomInfo(roomId).observe(getViewLifecycleOwner(), stateModel -> {
                    switch (stateModel.getStatus()) {
                        case SUCCESS:
                            mRoom = stateModel.getData();

                            tvTitle.setText(mRoom.getName());

                            mMemberAdapter = new VerticalMemberAdapter(mRoom.getMembers(), RoomProfileFragment.this);
                            mRvMembers.setAdapter(mMemberAdapter);

                            break;
                    }
                });

                ImageView civAvatar = root.findViewById(R.id.civAvatar);
                new AvatarLoader(mActivity, getViewLifecycleOwner())
                        .loadRoom(roomId, civAvatar);
            }
        }

        layoutRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRenameDialog();
            }
        });

        layoutChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = FileUtils.createImageIntent();
                startActivityForResult(intent, FileUtils.REQUEST_CODE_IMAGE);
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

                removeMember(item);
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(mRvMembers);
    }

    private void removeMember(IStudent student) {
        String studentCode = student.getCode();

        mViewModel.removeMember(mRoom, studentCode)
                .observe(getViewLifecycleOwner(), stateModel -> {
                    switch (stateModel.getStatus()) {
                        case ERROR:
                            final String FAILURE_MSG = "Xóa thành viên thất bại";
                            Toast.makeText(mActivity, FAILURE_MSG, Toast.LENGTH_SHORT).show();
                            break;

                        case SUCCESS:
                            // remove your self
                            if (studentCode.equals(User.getInstance().getStudentId())) {
                                final String SELF_REMOVE_MSG = "Thoát phòng chat thành công";
                                Toast.makeText(mActivity, SELF_REMOVE_MSG, Toast.LENGTH_SHORT).show();

                                mNavController.navigate(R.id.action_navigation_room_profile_to_navigation_chat);
                            } else {
                                final String REMOVE_MSG = "Xóa " + student.getName() + " khỏi phòng chat thành công";
                                Toast.makeText(mActivity, REMOVE_MSG, Toast.LENGTH_SHORT).show();
                            }

                            break;

                    }
                });
    }

    private void openRenameDialog() {
        RenameDialog dialog = new RenameDialog();

        Bundle bundle = new Bundle();
        bundle.putString("title", mRoom.getName());
        dialog.setArguments(bundle);

        dialog.setOnSubmitListener(new RenameDialog.OnSubmitListener() {
            @Override
            public void onSubmit(String title) {
                mViewModel.changeTitle(mRoom, title)
                        .observe(getViewLifecycleOwner(), new Observer<StateModel<String>>() {
                            @Override
                            public void onChanged(StateModel<String> stateModel) {
                                switch (stateModel.getStatus()) {
                                    case ERROR:
                                        Toast.makeText(mActivity, "Đổi tên phòng chat thất bại", Toast.LENGTH_SHORT).show();
                                        break;

                                    case SUCCESS:
                                        Toast.makeText(mActivity, "Đổi tên phòng chat thành công", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        });
            }
        });

        dialog.show(mActivity.getSupportFragmentManager(), RenameDialog.class.getName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case FileUtils.REQUEST_CODE_IMAGE:
                Uri uri = data.getData();
                Toast.makeText(mActivity, uri.getPath(), Toast.LENGTH_SHORT).show();

                mViewModel.changeAvatar(mRoom, uri);

                break;
        }
    }
}
