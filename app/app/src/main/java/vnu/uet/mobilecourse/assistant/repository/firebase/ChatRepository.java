package vnu.uet.mobilecourse.assistant.repository.firebase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import vnu.uet.mobilecourse.assistant.model.firebase.FirebaseCollectionName;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.Message_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;

public class ChatRepository implements IChatRepository {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;

    public ChatRepository() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public IStateLiveData<List<GroupChat_UserSubCol>> getAllUserGroupChats() {
        return null;
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
        IStateLiveData<GroupChat> createGroupState = new StateLiveData<>();
        createGroupState.postLoading();
        db.collection(FirebaseCollectionName.CHAT_GROUP).document(groupChat.getId()).set(groupChat).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> createGroup) {
                if (createGroup.isSuccessful()) {
                    createGroupState.postSuccess(groupChat);
                } else {
                    createGroupState.postError(createGroup.getException());
                }
            }
        });
        return createGroupState;
    }
}
