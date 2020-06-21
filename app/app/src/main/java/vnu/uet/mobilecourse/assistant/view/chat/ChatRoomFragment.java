package vnu.uet.mobilecourse.assistant.view.chat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.util.Util;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.MessageAdapter;
import vnu.uet.mobilecourse.assistant.exception.DocumentNotFoundException;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.model.firebase.Message_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.util.FirebaseStructureId;
import vnu.uet.mobilecourse.assistant.util.StringConst;
import vnu.uet.mobilecourse.assistant.viewmodel.ChatRoomViewModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class ChatRoomFragment extends Fragment {

    private MessageAdapter mMessageAdapter;
    private FragmentActivity mActivity;
    private ChatRoomViewModel mViewModel;
    private NavController mNavController;

    private String mCode, mTitle, mType, mRoomId;

    private TextView mEtMessage;

    private boolean initializeRoom = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);

        View root = inflater.inflate(R.layout.fragment_chat_room, container, false);

        mActivity = getActivity();

        if (mActivity != null) {
            mNavController = Navigation.findNavController(mActivity, R.id.nav_host_fragment);
        }

        mEtMessage= root.findViewById(R.id.etMessage);
        mEtMessage.setMovementMethod(new ScrollingMovementMethod());

        Bundle args = getArguments();
        if (args != null) {
            mTitle = args.getString("title");
            mViewModel.setTitle(mTitle);
            TextView tvChatGroupTitle = root.findViewById(R.id.tvRoomTitle);
            tvChatGroupTitle.setText(mTitle);

            RecyclerView rvChat = initializeListView(root);

            mCode = args.getString("code");
            mViewModel.setCode(mCode);

            mType = args.getString("type");
            mViewModel.setType(mType);

            mRoomId = args.getString("roomId");
            if (mRoomId == null && mType.equals(GroupChat.DIRECT)) {
                mRoomId = FirebaseStructureId.directedChat(mCode);
            }

            mViewModel.getAllMessages(mRoomId).observe(getViewLifecycleOwner(), new Observer<StateModel<List<Message_GroupChatSubCol>>>() {
                @Override
                public void onChanged(StateModel<List<Message_GroupChatSubCol>> stateModel) {
                    Log.d(ChatRoomFragment.class.getSimpleName(), "onChanged: " + stateModel.getStatus());

                    switch (stateModel.getStatus()) {
                        case SUCCESS:
                            List<Message_GroupChatSubCol> messages = stateModel.getData();

                            if (messages.isEmpty()) {
//                                if (mType.equals(GroupChat.DIRECT)) {
//                                    mViewModel.createDirectedChat();
//                                }
                                initializeRoom = true;
                            } else {
                                mMessageAdapter = new MessageAdapter(messages, ChatRoomFragment.this);
                                rvChat.setAdapter(mMessageAdapter);
                            }

                            break;

                        case ERROR:
                            Exception error = stateModel.getError();
                            if (error instanceof DocumentNotFoundException) {
                                Toast.makeText(getContext(), "Chat room not created yet.", Toast.LENGTH_SHORT).show();
                                Log.d(ChatRoomFragment.class.getSimpleName(), "Chat room not created yet.");
                            } else {
                                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d(ChatRoomFragment.class.getSimpleName(), "Error: " + error.getMessage());
                            }

                            break;
                    }
                }
            });
        }

        ImageButton btnSend = root.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        initializeToolbar(root);

        return root;
    }

    @SuppressLint("RestrictedApi")
    private void sendMessage() {
        String content = mEtMessage.getText().toString();
        Log.d("CHAT", "sendMessage: " + content);

        Message_GroupChatSubCol message = new Message_GroupChatSubCol();
        message.setFromId(User.getInstance().getStudentId());
        message.setFromName(User.getInstance().getName());
        message.setTimestamp(System.currentTimeMillis() / 1000);
        message.setContent(content);
        message.setId(Util.autoId());

        mViewModel.sendMessage(mRoomId, message, initializeRoom).observe(getViewLifecycleOwner(), new Observer<StateModel<String>>() {
            @Override
            public void onChanged(StateModel<String> stateModel) {
                switch (stateModel.getStatus()) {
                    case SUCCESS:
                        break;

                    case ERROR:
                        Toast.makeText(mActivity, "Không thể gửi tin nhắn - "
                                + stateModel.getError().getMessage(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        mEtMessage.setText(StringConst.EMPTY);
    }

    private Toolbar initializeToolbar(View root) {
        Toolbar toolbar = null;

        if (mActivity instanceof AppCompatActivity) {
            toolbar = root.findViewById(R.id.toolbar);
            toolbar.setTitle(StringConst.EMPTY);
            ((AppCompatActivity) mActivity).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);
        }

        return toolbar;
    }

    private void navigateFriendProfile() {
        Bundle bundle = new Bundle();
        bundle.putString("name", mTitle);
        bundle.putString("code", mCode);
//        bundle.putString("avatar", current.getAvatar());
        bundle.putBoolean("active", true);
        bundle.putBoolean("fromChat", true);

        mNavController.navigate(R.id.action_navigation_chat_room_to_navigation_friend_profile, bundle);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.chat_room_toolbar_menu, menu);
        Log.d(getTag(), "onCreateOptionsMenu: ");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.action_view_info) {
            navigateFriendProfile();
            Log.d(getTag(), "onOptionsItemSelected: ");
        }

        return super.onOptionsItemSelected(item);
    }

    private RecyclerView initializeListView(View root) {
        RecyclerView rvChat = root.findViewById(R.id.rvChat);
        rvChat.setHasFixedSize(true);

        rvChat.setAdapter(mMessageAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        linearLayoutManager.setStackFromEnd(true);

        rvChat.setLayoutManager(linearLayoutManager);

        return rvChat;
    }
}
