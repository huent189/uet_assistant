package vnu.uet.mobilecourse.assistant.database.DAO;

import com.google.firebase.firestore.CollectionReference;

import java.util.List;

import vnu.uet.mobilecourse.assistant.model.firebase.Member_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;

public class Member_GroupChatSubColDAO extends FirebaseDAO<Member_GroupChatSubCol> {
    /**
     * DAO usually interact in an collection/sub collection
     *
     * @param colRef reference of the corresponding collection
     */
    Member_GroupChatSubColDAO(CollectionReference colRef) {
        super(colRef);
    }

    @Override
    public StateLiveData<List<Member_GroupChatSubCol>> readAll() {
        return null;
    }
}
