package vnu.uet.mobilecourse.assistant.database.DAO;

import com.google.firebase.firestore.DocumentSnapshot;

import vnu.uet.mobilecourse.assistant.model.firebase.IFirebaseModel;
import vnu.uet.mobilecourse.assistant.model.firebase.Message_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;

public class MessageDAO extends FirebaseDAO<Message_GroupChatSubCol> {

    MessageDAO(String collectionName) {
        super(collectionName);
    }

    @Override
    protected Message_GroupChatSubCol fromSnapshot(DocumentSnapshot snapshot) {
        return snapshot.toObject(Message_GroupChatSubCol.class);
    }
}