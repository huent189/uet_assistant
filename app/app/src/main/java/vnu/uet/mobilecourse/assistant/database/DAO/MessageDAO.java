package vnu.uet.mobilecourse.assistant.database.DAO;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import vnu.uet.mobilecourse.assistant.model.firebase.FirebaseCollectionName;
import vnu.uet.mobilecourse.assistant.model.firebase.IFirebaseModel;
import vnu.uet.mobilecourse.assistant.model.firebase.Message_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;

public class MessageDAO extends FirebaseDAO {

    public MessageDAO() {
        super(null);
    }

    @Override
    protected Message_GroupChatSubCol fromSnapshot(DocumentSnapshot snapshot) {
        return snapshot.toObject(Message_GroupChatSubCol.class);
    }

    @Override
    public StateLiveData<Message_GroupChatSubCol> add(String id, Object document) {
        return null;
    }
}
