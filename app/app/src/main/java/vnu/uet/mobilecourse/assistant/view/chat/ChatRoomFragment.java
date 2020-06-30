package vnu.uet.mobilecourse.assistant.view.chat;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

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
import vnu.uet.mobilecourse.assistant.adapter.MentionAdapter;
import vnu.uet.mobilecourse.assistant.adapter.MessageAdapter;
import vnu.uet.mobilecourse.assistant.exception.DocumentNotFoundException;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.model.firebase.Member_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.Message_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.util.AvatarLoader;
import vnu.uet.mobilecourse.assistant.util.FirebaseStructureId;
import vnu.uet.mobilecourse.assistant.util.StringConst;
import vnu.uet.mobilecourse.assistant.viewmodel.ChatRoomViewModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class ChatRoomFragment extends Fragment {

    private MessageAdapter mMessageAdapter;
    private FragmentActivity mActivity;
    private ChatRoomViewModel mViewModel;
    private NavController mNavController;

    private TextView mTvRoomTitle;
    private MultiAutoCompleteTextView mEtMessage;
    private MenuItem mViewInfoItem;
    private ImageView mCivAvatar;

    private ArrayAdapter<Member_GroupChatSubCol> mMemberListAdapter;

    private String mCode, mTitle, mType, mRoomId;
    private boolean mEmptyRoom = false;
    private String[] mMemberIds;
    private GroupChat mRoom;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);

        View root = inflater.inflate(R.layout.fragment_chat_room, container, false);

        mActivity = getActivity();

        if (mActivity != null) {
            mNavController = Navigation.findNavController(mActivity, R.id.nav_host_fragment);
        }

        mEtMessage = root.findViewById(R.id.etMessage);
        mEtMessage.setMovementMethod(new ScrollingMovementMethod());

        mCivAvatar = root.findViewById(R.id.civAvatar);

        Bundle args = getArguments();
        if (args != null) {
            mTitle = args.getString("title");
            mTvRoomTitle = root.findViewById(R.id.tvRoomTitle);
            mTvRoomTitle.setText(mTitle);

            RecyclerView rvChat = initializeListView(root);

            mType = args.getString("type");
            mRoomId = args.getString("roomId");
            mCode = args.getString("code");

            if (GroupChat.DIRECT.equals(mType)) {
                setupDirectChat();
            } else {
                setupGroupChat();
            }

            mViewModel.markAsSeen(mRoomId);

            mViewModel.getAllMessages(mRoomId)
                    .observe(getViewLifecycleOwner(), stateModel -> {
                        Log.d(ChatRoomFragment.class.getSimpleName(), "onChanged: " + stateModel.getStatus());

                        switch (stateModel.getStatus()) {
                            case SUCCESS:
                                List<Message_GroupChatSubCol> messages = stateModel.getData();

                                if (messages.isEmpty()) {
                                    mEmptyRoom = true;
                                } else {
                                    mMessageAdapter = new MessageAdapter(messages, ChatRoomFragment.this);
                                    rvChat.setAdapter(mMessageAdapter);
                                    mEmptyRoom = false;
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
                    });
        }

        ImageButton btnSend = root.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(v -> sendTextMessage());

        initializeToolbar(root);

        return root;
    }

    private void setupDirectChat() {
        if (mRoomId == null) {
            mRoomId = FirebaseStructureId.directedChat(mCode);
        }

        if (mCode == null) {
            mCode = FirebaseStructureId.getMateId(mRoomId);
        }

        new AvatarLoader(mActivity, getViewLifecycleOwner())
                .loadUser(mCode, mCivAvatar);

        mMemberIds = new String[] {User.getInstance().getStudentId(), mCode};
    }

    private void setupGroupChat() {
        setupMention();

        new AvatarLoader(mActivity, getViewLifecycleOwner())
                .loadUser(mRoomId, mCivAvatar);

        mViewModel.getRoomInfo(mRoomId).observe(getViewLifecycleOwner(), stateModel -> {
            switch (stateModel.getStatus()) {
                case SUCCESS:
                    mRoom = stateModel.getData();

                    List<Member_GroupChatSubCol> members = mRoom.getMembers();
                    mMemberIds = members.stream()
                            .map(Member_GroupChatSubCol::getId)
                            .toArray(String[]::new);

                    mMemberListAdapter = new MentionAdapter(mActivity, members);
                    mEtMessage.setAdapter(mMemberListAdapter);

                    mTitle = mRoom.getName();
                    mTvRoomTitle.setText(mTitle);

                    if (mViewInfoItem != null) mViewInfoItem.setEnabled(true);

                    break;

                case ERROR:
                    Toast.makeText(mActivity, "Không lấy được thông tin phòng chat", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void setupMention() {
        mEtMessage.setThreshold(1);

        // Create a new Tokenizer which will get text after '@' and terminate on ' '
        mEtMessage.setTokenizer(new MultiAutoCompleteTextView.Tokenizer() {

            @Override
            public CharSequence terminateToken(CharSequence text) {
                int i = text.length();

                while (i > 0 && text.charAt(i - 1) == StringConst.SPACE_CHAR) {
                    i--;
                }

                if (i > 0 && text.charAt(i - 1) == StringConst.SPACE_CHAR) {
                    return text;
                } else {
                    if (text instanceof Spanned) {
                        SpannableString sp = new SpannableString(text + StringConst.SPACE);
                        TextUtils.copySpansFrom((Spanned) text, 0, text.length(), Object.class, sp, 0);
                        return sp;
                    } else {
                        return text + StringConst.SPACE;
                    }
                }
            }

            @Override
            public int findTokenStart(CharSequence text, int cursor) {
                int i = cursor;

                while (i > 0 && text.charAt(i - 1) != StringConst.AT_SIGN
                        && text.charAt(i - 1) != StringConst.SPACE_CHAR
                        && text.charAt(i - 1) != StringConst.LINE_BREAK_CHAR) {
                    i--;
                }

                //Check if token really started with @, else we don't have a valid token
                if (i < 1 || text.charAt(i - 1) != StringConst.AT_SIGN) {
                    return cursor;
                }

                return i;
            }

            @Override
            public int findTokenEnd(CharSequence text, int cursor) {
                int i = cursor;
                int len = text.length();

                while (i < len) {
                    if (text.charAt(i) == StringConst.AT_SIGN || text.charAt(i) == StringConst.LINE_BREAK_CHAR) {
                        return i;
                    } else {
                        i++;
                    }
                }

                return len;
            }
        });

        mEtMessage.setDropDownBackgroundResource(R.drawable.list_item_background);
    }

    private void sendTextMessage() {
        String content = mEtMessage.getText().toString();
        Log.d("CHAT", "sendMessage: " + content);

        Message_GroupChatSubCol message = mViewModel.generateTextMessage(content, mMemberIds);

        // first message in directed chat
        if (GroupChat.DIRECT.equals(mType) && mEmptyRoom) {
            mViewModel.connectAndSendMessage(mRoomId, mTitle, message, mMemberIds)
                    .observe(getViewLifecycleOwner(), stateModel -> {
                        switch (stateModel.getStatus()) {
                            case SUCCESS:
                                if (stateModel.getData().equals(ChatRoomViewModel.CONNECTED_MSG)) {
                                    Toast.makeText(mActivity, stateModel.getData(), Toast.LENGTH_SHORT).show();
                                }

                                break;

                            case ERROR:
                                Toast.makeText(mActivity, "Không thể gửi tin nhắn - "
                                        + stateModel.getError().getMessage(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    });
        }
        // other cases
        else {
            mViewModel.sendMessage(mRoomId, message, mMemberIds)
                    .observe(getViewLifecycleOwner(), new Observer<StateModel<String>>() {
                        @Override
                        public void onChanged(StateModel<String> stateModel) {
                            switch (stateModel.getStatus()) {
                                case ERROR:
                                    Toast.makeText(mActivity, "Không thể gửi tin nhắn - "
                                            + stateModel.getError().getMessage(), Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    });
        }


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

    private void navigateRoomProfile() {
        if (mRoom != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("room", mRoom);

            mNavController.navigate(R.id.action_navigation_chat_room_to_navigation_room_profile, bundle);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.chat_room_toolbar_menu, menu);

        mViewInfoItem = menu.findItem(R.id.action_view_info);

        if (GroupChat.GROUP.equals(mType)) {
            mViewInfoItem.setEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.action_view_info) {
            switch (mType) {
                case GroupChat.DIRECT:
                    navigateFriendProfile();
                    break;

                case GroupChat.GROUP:
                    navigateRoomProfile();
                    break;
            }
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
