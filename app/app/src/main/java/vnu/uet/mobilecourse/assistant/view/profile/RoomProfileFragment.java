package vnu.uet.mobilecourse.assistant.view.profile;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.viewmodel.RoomProfileViewModel;
import vnu.uet.mobilecourse.assistant.adapter.ClassMateAdapter;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RoomProfileFragment extends Fragment {

    private RoomProfileViewModel mViewModel;
    private GroupChat mRoom;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(RoomProfileViewModel.class);

        View root = inflater.inflate(R.layout.fragment_room_profile, container, false);

        if (getArguments() != null) {
            mRoom = getArguments().getParcelable("room");

            if (mRoom != null) {
                TextView tvTitle = root.findViewById(R.id.tvTitle);
                tvTitle.setText(mRoom.getName());

                RecyclerView rvMembers = root.findViewById(R.id.rvMembers);

                String roomId = mRoom.getId();

                mViewModel.getRoomInfo(roomId).observe(getViewLifecycleOwner(), new Observer<StateModel<GroupChat>>() {
                    @Override
                    public void onChanged(StateModel<GroupChat> stateModel) {
                        switch (stateModel.getStatus()) {
                            case SUCCESS:
                                mRoom = stateModel.getData();

                                ClassMateAdapter adapter = new ClassMateAdapter(mRoom.getMembers(), RoomProfileFragment.this);
                                rvMembers.setAdapter(adapter);

                                break;
                        }
                    }
                });

            }
        }

        return root;
    }
}
