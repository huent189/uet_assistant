package vnu.uet.mobilecourse.assistant.database.DAO;

import com.google.firebase.firestore.DocumentSnapshot;

import vnu.uet.mobilecourse.assistant.model.firebase.FirebaseCollectionName;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;

public class GroupChatDAO extends FirebaseDAO<GroupChat> {
    public GroupChatDAO() {
        super(FirebaseCollectionName.GROUP_CHAT);
    }

    @Override
    protected GroupChat fromSnapshot(DocumentSnapshot snapshot) {
        return snapshot.toObject(GroupChat.class);
    }
}
