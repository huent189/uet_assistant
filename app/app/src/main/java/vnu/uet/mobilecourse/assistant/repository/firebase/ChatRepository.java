package vnu.uet.mobilecourse.assistant.repository.firebase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import androidx.lifecycle.Observer;
import vnu.uet.mobilecourse.assistant.database.DAO.FirebaseCollectionName;
import vnu.uet.mobilecourse.assistant.database.DAO.GroupChatDAO;
import vnu.uet.mobilecourse.assistant.database.DAO.GroupChat_UserSubColDAO;
import vnu.uet.mobilecourse.assistant.database.DAO.Message_GroupChatSubColDAO;
import vnu.uet.mobilecourse.assistant.exception.DocumentNotFoundException;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.Message_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class ChatRepository implements IChatRepository {

    private FirebaseFirestore db;

    private GroupChat_UserSubColDAO mUserGroupChatDAO;
    private Message_GroupChatSubColDAO mMessageDAO;

    private static ChatRepository instance;

    public static ChatRepository getInstance() {
        if (instance == null) {
            instance = new ChatRepository();
        }

        return instance;
    }

    public ChatRepository() {
        db = FirebaseFirestore.getInstance();
        mUserGroupChatDAO = new GroupChat_UserSubColDAO();
    }

    @Override
    public IStateLiveData<List<GroupChat_UserSubCol>> getAllUserGroupChats() {
        return mUserGroupChatDAO.readAll();
    }

    @Override
    public IStateLiveData<GroupChat> getGroupChatInfo(String groupId) {
        return null;
    }

    @Override
    public IStateLiveData<List<Message_GroupChatSubCol>> getMessages(String groupId) {
        mMessageDAO = new Message_GroupChatSubColDAO(groupId);
        return mMessageDAO.readAll();
    }


    @Override
    public StateLiveData<String> sendMessage(String groupId, Message_GroupChatSubCol message) {
        DocumentReference messRef = db
                .collection(FirebaseCollectionName.GROUP_CHAT)
                .document(groupId)
                .collection(FirebaseCollectionName.MESSAGE)
                .document(message.getId());

        StateLiveData<String> sendStatus = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));

        messRef.set(message)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        sendStatus.postError(e);
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // update last message to group chat in user col
                        db.collection(FirebaseCollectionName.USER)
                                .document(User.getInstance().getStudentId()) // user DocRef
                                .collection(FirebaseCollectionName.GROUP_CHAT) // subCollection
                                .document(groupId) // group DocRef
                                .update("lastMessage", message.getContent(),
                                        "lastMessageTime", message.getTimestamp())
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        sendStatus.postError(e);
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        sendStatus.postSuccess("success");
                                    }
                                });
                    }
                });

        return sendStatus;
    }

    @Override
    public StateMediatorLiveData<String> createGroupChat(GroupChat groupChat) {
        StateLiveData<GroupChat> createGroupState = new GroupChatDAO().add( groupChat.getId(), groupChat);
        StateLiveData<String> addGroup = new GroupChat_UserSubColDAO().addGroupChat(groupChat);
        StateLiveData<String> addMember = new GroupChatDAO().addMembers(groupChat.getId(), groupChat.getMembers());

        return new CreateGroupChatState(createGroupState, addMember, addGroup);
    }


    static class CreateGroupChatState extends StateMediatorLiveData<String> {
        private boolean createGroupState = false;
        private boolean addMemberToGroupState = false;
        private boolean addGroupToMemberState = false;

        CreateGroupChatState(@NonNull StateLiveData<GroupChat> createGroupLiveData,
                             @NonNull StateLiveData<String> addMemberLiveData,
                             @NonNull StateLiveData<String> addGroupLiveData) {
            super(new StateModel<>(StateStatus.LOADING));
            addSource(createGroupLiveData, stateModel -> {
                switch (stateModel.getStatus()) {
                    case ERROR:
                        createGroupState = false;
                        postError(stateModel.getError());
                        break;
                    case SUCCESS:
                        createGroupState = true;
                        break;
                    case LOADING:
                        createGroupState = false;
                        postLoading();
                        break;
                }
                if (createGroupState && addMemberToGroupState && addGroupToMemberState) {
                    postSuccess("create group success!");
                }
            });

            addSource(addMemberLiveData, stateModel -> {
                switch (stateModel.getStatus()) {
                    case ERROR:
                        addMemberToGroupState = false;
                        postError(stateModel.getError());
                        break;
                    case SUCCESS:
                        addMemberToGroupState = true;
                        break;
                    case LOADING:
                        addMemberToGroupState = false;
                        postLoading();
                        break;
                }
                if (createGroupState && addMemberToGroupState && addGroupToMemberState) {
                    postSuccess("create group success!");
                }
            });

            addSource(addGroupLiveData, stateModel -> {
                switch (stateModel.getStatus()) {
                    case ERROR:
                        addGroupToMemberState = false;
                        postError(stateModel.getError());
                        break;
                    case SUCCESS:
                        addGroupToMemberState = true;
                        break;
                    case LOADING:
                        addGroupToMemberState = false;
                        postLoading();
                        break;
                }
                if (createGroupState && addMemberToGroupState && addGroupToMemberState) {
                    postSuccess("create group success!");
                }
            });
        }
    }

    @Deprecated
    static class ConnectedCheckingLiveData extends StateMediatorLiveData<Boolean> {

        public ConnectedCheckingLiveData(StateLiveData<GroupChat_UserSubCol> readLiveData) {
            postLoading();

            addSource(readLiveData, new Observer<StateModel<GroupChat_UserSubCol>>() {
                @Override
                public void onChanged(StateModel<GroupChat_UserSubCol> stateModel) {
                    switch (stateModel.getStatus()) {
                        case LOADING:
                            postLoading();
                            break;

                        case ERROR:
                            Exception exception = stateModel.getError();

                            if (exception instanceof DocumentNotFoundException) {
                                postSuccess(Boolean.FALSE);
                            } else {
                                postError(exception);
                            }

                            break;

                        case SUCCESS:
                            postSuccess(Boolean.TRUE);
                            break;
                    }
                }
            });
        }
    }
}