package vnu.uet.mobilecourse.assistant.database.DAO;

import com.google.firebase.firestore.DocumentSnapshot;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;

public class GroupChatDAO extends FirebaseDAO<GroupChat> {
    public GroupChatDAO() {
        super(null);
    }

    @Override
    protected GroupChat fromSnapshot(DocumentSnapshot snapshot) {
        return snapshot.toObject(GroupChat.class);
    }
}
