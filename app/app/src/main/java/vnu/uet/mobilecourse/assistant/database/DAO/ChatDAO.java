package vnu.uet.mobilecourse.assistant.database.DAO;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;


import java.util.List;

import vnu.uet.mobilecourse.assistant.model.IStudent;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.Member_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class ChatDAO {
    private FirebaseFirestore db;
    private CollectionReference groupChatCol;
    private CollectionReference userCol;

    public ChatDAO() {
        db = FirebaseFirestore.getInstance();
        groupChatCol = db.collection(FirebaseCollectionName.GROUP_CHAT);
        userCol = db.collection(FirebaseCollectionName.USER);
    }

    public StateLiveData<String> addMember(GroupChat_UserSubCol group, List<Member_GroupChatSubCol> members) {
        StateLiveData<String> addMemberState = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));
        WriteBatch batch = db.batch();

        for (IStudent member : members) {
            // add member to group:
            DocumentReference memberDocRef = groupChatCol.document(group.getId())
                    .collection(FirebaseCollectionName.MEMBER)
                    .document(member.getCode());
            batch.set(memberDocRef, member);

            // add group to member:
            DocumentReference groupDocRef = userCol.document(member.getCode())
                    .collection(FirebaseCollectionName.GROUP_CHAT)
                    .document(group.getId());
            batch.set(groupDocRef, group);
        }

        batch.commit().addOnFailureListener(e -> {
            addMemberState.postError(e);
        }).addOnSuccessListener(aVoid -> {
           addMemberState.postSuccess("add member success");
        });

        return addMemberState;
    }

    public StateLiveData<String> removeMember(String groupId, String memberId) {
        StateLiveData<String> removeMemberState = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));
        WriteBatch batch = db.batch();

        // remove member from groupChat
        batch.delete(groupChatCol.document(groupId).collection(FirebaseCollectionName.MEMBER).document(memberId));

        // remove group from member
        batch.delete(userCol.document(memberId).collection(FirebaseCollectionName.GROUP_CHAT).document(groupId));

        batch.commit().addOnFailureListener(e -> {
            removeMemberState.postError(e);
        }).addOnSuccessListener(aVoid -> {
            removeMemberState.postSuccess(memberId);
        });

        return removeMemberState;
    }

    public StateLiveData<String> changeGroupTitle(String groupId, String[] memberIds, String title){
        StateLiveData<String> changeGroupTitleState = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));

        WriteBatch batch = db.batch();

        for (String memberId :
                memberIds) {
            DocumentReference groupChatDocRef = userCol.document(memberId)
                    .collection(FirebaseCollectionName.GROUP_CHAT)
                    .document(groupId);
            batch.update(groupChatDocRef, "name",  title);
        }

        batch.commit().addOnSuccessListener(aVoid -> {
            changeGroupTitleState.postSuccess(title);
        }).addOnFailureListener(e -> {
            changeGroupTitleState.postError(e);
        });
        return changeGroupTitleState;
    }
}
