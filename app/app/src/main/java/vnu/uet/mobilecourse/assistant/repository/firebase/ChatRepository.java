package vnu.uet.mobilecourse.assistant.repository.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import vnu.uet.mobilecourse.assistant.database.DAO.GroupChatDAO;
import vnu.uet.mobilecourse.assistant.database.DAO.GroupChat_UserSubColDAO;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.Message_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public class ChatRepository implements IChatRepository {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore mDb;

    private GroupChat_UserSubColDAO mGroupChatDAO;

    private static ChatRepository instance;

    public static ChatRepository getInstance() {
        if (instance == null) {
            instance = new ChatRepository();
        }

        return instance;
    }

    public ChatRepository() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDb = FirebaseFirestore.getInstance();

        mGroupChatDAO = new GroupChat_UserSubColDAO();
    }

    @Override
    public IStateLiveData<List<GroupChat_UserSubCol>> getAllUserGroupChats() {
        return mGroupChatDAO.readAll();
    }

    @Override
    public IStateLiveData<GroupChat> getGroupChatInfo(String id) {
        return null;
    }

    @Override
    public IStateLiveData<List<Message_GroupChatSubCol>> getMessages(String id) {
        return null;
    }

    @Override
    public IStateLiveData<String> sendMessage(String groupId, Message_GroupChatSubCol message) {
        return null;
    }

    @Override
    public IStateLiveData<GroupChat> createGroupChat(GroupChat groupChat) {
        return new GroupChatDAO().add(groupChat.getId(), groupChat);
    }
}