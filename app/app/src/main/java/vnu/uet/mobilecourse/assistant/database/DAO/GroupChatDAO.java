package vnu.uet.mobilecourse.assistant.database.DAO;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

import vnu.uet.mobilecourse.assistant.model.firebase.FirebaseCollectionName;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;

public class GroupChatDAO extends FirebaseDAO<GroupChat> {
    public GroupChatDAO() {
        super(null);
    }


    @Override
    public StateLiveData<List<GroupChat>> readAll() {
        return null;
    }
}
