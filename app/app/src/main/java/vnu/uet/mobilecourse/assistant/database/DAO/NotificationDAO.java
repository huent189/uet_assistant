package vnu.uet.mobilecourse.assistant.database.DAO;

import com.google.firebase.firestore.DocumentSnapshot;

import vnu.uet.mobilecourse.assistant.model.firebase.FirebaseCollectionName;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;

public class NotificationDAO extends FirebaseDAO<Todo> {

//    public NotificationDAO() {
        super(FirebaseCollectionName.);
//    }

    @Override
    protected Todo fromSnapshot(DocumentSnapshot snapshot) {
        return snapshot.toObject(Todo.class);
    }
}
