package vnu.uet.mobilecourse.assistant.database.DAO;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import vnu.uet.mobilecourse.assistant.model.firebase.FirebaseCollectionName;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;

public class GroupChatDAO extends FirebaseDAO<GroupChat> {
    public GroupChatDAO() {
        super(FirebaseFirestore.getInstance().collection(FirebaseCollectionName.GROUP_CHAT));
    }

    @Override
    protected GroupChat fromSnapshot(DocumentSnapshot snapshot) {
        return snapshot.toObject(GroupChat.class);
    }
}
