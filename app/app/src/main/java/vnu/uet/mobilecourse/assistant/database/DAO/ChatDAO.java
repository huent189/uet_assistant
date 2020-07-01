package vnu.uet.mobilecourse.assistant.database.DAO;

import android.annotation.SuppressLint;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.util.Util;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import vnu.uet.mobilecourse.assistant.model.IStudent;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.Member_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.Message_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.util.FileUtils;
import vnu.uet.mobilecourse.assistant.util.StringUtils;
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

    public StateLiveData<String> addMember(GroupChat_UserSubCol group, String[] oldMemberIds, List<Member_GroupChatSubCol> members) {
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

        String[] memberIds = new String[oldMemberIds.length + members.size()];
        System.arraycopy(oldMemberIds, 0, memberIds, 0, oldMemberIds.length);
        System.arraycopy(members.stream().map(Member_GroupChatSubCol::getId).toArray(String[]::new),
                0, memberIds, oldMemberIds.length, members.size());

        String simpleName = StringUtils.getLastSegment(USERNAME, 2);
        appendAdminMessage(batch, group.getId(), memberIds, simpleName + " đã thêm " + members.size() + " thành viên mới");

        batch.commit().addOnFailureListener(e -> {
            addMemberState.postError(e);
        }).addOnSuccessListener(aVoid -> {
           addMemberState.postSuccess("add member success");
        });

        return addMemberState;
    }

    public StateLiveData<String> removeMember(String groupId, String[] memberIds, String memberId) {
        StateLiveData<String> removeMemberState = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));
        WriteBatch batch = db.batch();

        // remove member from groupChat
        batch.delete(groupChatCol.document(groupId).collection(FirebaseCollectionName.MEMBER).document(memberId));

        // remove group from member
        batch.delete(userCol.document(memberId).collection(FirebaseCollectionName.GROUP_CHAT).document(groupId));

        String[] newMemberIds = new String[memberIds.length - 1];
        int i = 0;
        for (String id : memberIds) {
            if (!id.equals(memberId)) {
                newMemberIds[i++] = id;
            }
        }

        String simpleName = StringUtils.getLastSegment(USERNAME, 2);
        appendAdminMessage(batch, groupId, newMemberIds, simpleName + " đã xóa " + memberId + " khỏi phòng");

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

        String message = StringUtils.getLastSegment(USERNAME, 2) + " đã đổi tên phòng chat";
        batch = appendAdminMessage(batch, groupId, memberIds, message);

        batch.commit().addOnSuccessListener(aVoid -> {
            changeGroupTitleState.postSuccess(title);
        }).addOnFailureListener(e -> {
            changeGroupTitleState.postError(e);
        });
        return changeGroupTitleState;
    }

    @SuppressLint("RestrictedApi")
    public WriteBatch appendAdminMessage(WriteBatch batch, String roomId, String[] memberIds, String content) {
        // generate message entity
        Message_GroupChatSubCol message = new Message_GroupChatSubCol();
        message.setContentType(FileUtils.MIME_TEXT);
        message.setTimestamp(System.currentTimeMillis() / 1000);
        message.setFromName("admin");
        message.setFromId("admin");
        message.setContent(content);
        message.setId(Util.autoId());

        DocumentReference newMsgRef = groupChatCol.document(roomId)
                .collection(FirebaseCollectionName.MESSAGE)
                .document(message.getId());

        batch.set(newMsgRef, message);

        for (String memberId : memberIds) {
            DocumentReference groupChatDocRef = userCol.document(memberId)
                    .collection(FirebaseCollectionName.GROUP_CHAT)
                    .document(roomId);
            batch.update(groupChatDocRef, "lastMessage", content,
                    "lastMessageTime", message.getTimestamp(),
                    "seen", false);
        }

        return batch;
    }

    private static final String USERNAME = User.getInstance().getName();
}
