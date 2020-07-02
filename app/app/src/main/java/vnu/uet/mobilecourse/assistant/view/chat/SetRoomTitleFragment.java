package vnu.uet.mobilecourse.assistant.view.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.VerticalMemberAdapter;
import vnu.uet.mobilecourse.assistant.model.IStudent;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.viewmodel.AddGroupChatViewModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

import java.util.List;
import java.util.Locale;

public class SetRoomTitleFragment extends Fragment {

    private AddGroupChatViewModel mViewModel;
    private FragmentActivity mActivity;
    private NavController mNavController;
    private EditText mEtRoomTitle;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mActivity = getActivity();
        if (mActivity != null) {
            mViewModel = new ViewModelProvider(mActivity).get(AddGroupChatViewModel.class);
            mNavController = Navigation.findNavController(mActivity, R.id.nav_host_fragment);
        }

        View root = inflater.inflate(R.layout.fragment_set_room_title, container, false);

        initializeToolbar(root);

        mEtRoomTitle = root.findViewById(R.id.etRoomTitle);
        mEtRoomTitle.requestFocus();

        RecyclerView rvMembers = root.findViewById(R.id.rvMembers);
        TextView tvMemberCounter = root.findViewById(R.id.tvMemberCounter);

        mViewModel.getSelectedList().observe(getViewLifecycleOwner(), new Observer<List<IStudent>>() {
            @Override
            public void onChanged(List<IStudent> students) {
                VerticalMemberAdapter adapter = new VerticalMemberAdapter(students, SetRoomTitleFragment.this);
                rvMembers.setAdapter(adapter);

                int memberCounter = students.size();
                tvMemberCounter.setText(String.format(Locale.ROOT, "%d thành viên", memberCounter));
            }
        });

        return root;
    }

    private Toolbar initializeToolbar(View root) {
        Toolbar toolbar = null;

        if (mActivity instanceof AppCompatActivity) {
            toolbar = root.findViewById(R.id.toolbar);
            toolbar.setTitle(R.string.title_chat_set_room_title);
            ((AppCompatActivity) mActivity).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);
        }

        return toolbar;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.create_group_chat_toolbar_menu, menu);

        MenuItem item = menu.findItem(R.id.action_next);
        item.setTitle("OK");

        Log.d(getTag(), "onCreateOptionsMenu: ");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.action_next) {
            String title = mEtRoomTitle.getText().toString();
            title = title.trim();

            if (title.isEmpty()) {
                Toast.makeText(mActivity, "Bạn chưa đặt tên nhóm", Toast.LENGTH_SHORT).show();
            } else {
                mViewModel.createGroupChat(title).observe(getViewLifecycleOwner(), new Observer<StateModel<GroupChat>>() {
                    @Override
                    public void onChanged(StateModel<GroupChat> stateModel) {
                        switch (stateModel.getStatus()) {
                            case SUCCESS:
                                GroupChat chat = stateModel.getData();

                                Bundle bundle = new Bundle();
                                bundle.putString("title", chat.getName());
                                bundle.putString("type", GroupChat.GROUP);
                                bundle.putString("roomId", chat.getId());

                                mNavController.navigate(R.id.action_navigation_set_room_title_to_navigation_chat_room, bundle);
                                mViewModel.clearData();

                                break;

                            case ERROR:
                                Toast.makeText(mActivity, "Không thể tạo nhóm trò chuyện", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
            }

            Log.d(getTag(), "onOptionsItemSelected: ");
        }

        return super.onOptionsItemSelected(item);
    }

}
