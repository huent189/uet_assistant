package vnu.uet.mobilecourse.assistant.repository.firebase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;

import vnu.uet.mobilecourse.assistant.SharedPreferencesManager;
import vnu.uet.mobilecourse.assistant.database.DAO.FirebaseCollectionName;
import vnu.uet.mobilecourse.assistant.database.DAO.GroupChatDAO;
import vnu.uet.mobilecourse.assistant.database.DAO.GroupChat_UserSubColDAO;
import vnu.uet.mobilecourse.assistant.database.DAO.Message_GroupChatSubColDAO;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.Member_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.Message_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
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
    public IStateLiveData<String> sendMessage(String groupId, Message_GroupChatSubCol message) {
        DocumentReference messRef = db.collection(FirebaseCollectionName.GROUP_CHAT).document(groupId).collection(FirebaseCollectionName.MESSAGE).document(message.getId());
        IStateLiveData<String> sendStatus = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));
        messRef.set(message).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                sendStatus.postError(e);
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                // update last message to group chat in user col
                db.collection(FirebaseCollectionName.USER).document(SharedPreferencesManager.getStringValue(SharedPreferencesManager.USER_ID)) // user DocRef
                        .collection(FirebaseCollectionName.GROUP_CHAT) // subCollection
                        .document(groupId) // group DocRef
                        .update("lastMessage", message.getContent(),
                                "lastMessageTime", message.getTimestamp()).addOnFailureListener(new OnFailureListener() {
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
    public IStateLiveData<GroupChat> createGroupChat(GroupChat groupChat) {
        // create group chat
        StateLiveData<GroupChat> groupChatStateLiveData = new GroupChatDAO().add(groupChat.getId(), groupChat);

        // add member to group chat
        WriteBatch batch = db.batch();
        for (Member_GroupChatSubCol member:
             groupChat.getMembers()) {
            DocumentReference memberRef = db.collection(FirebaseCollectionName.GROUP_CHAT).document(groupChat.getId()).collection(FirebaseCollectionName.MEMBER).document(member.getId());
            batch.set(memberRef, member);
        }

        batch.commit().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                groupChatStateLiveData.postError(e);
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // add group chat to member
                GroupChat_UserSubCol groupChat_userSubCol = new GroupChat_UserSubCol();
                groupChat_userSubCol.setId(groupChat.getId());
                groupChat_userSubCol.setName(groupChat.getName());
                groupChat_userSubCol.setSeen(false);
                db.collection(FirebaseCollectionName.USER).document(SharedPreferencesManager.getStringValue(SharedPreferencesManager.USER_ID)) // user DocRef
                        .collection(FirebaseCollectionName.GROUP_CHAT) // subCollection
                        .document(groupChat.getId()) // group DocRef
                        .set(groupChat_userSubCol).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        groupChatStateLiveData.postError(e);
                    }
                });
            }
        });

        return groupChatStateLiveData;
    }
}