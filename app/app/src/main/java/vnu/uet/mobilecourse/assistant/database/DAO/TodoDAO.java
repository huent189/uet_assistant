package vnu.uet.mobilecourse.assistant.database.DAO;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import vnu.uet.mobilecourse.assistant.model.firebase.FirebaseCollectionName;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;

public class TodoDAO extends FirebaseDAO<Todo> {

    public TodoDAO() {
        super(FirebaseFirestore.getInstance().collection(FirebaseCollectionName.TODO));
    }

    @Override
    protected Todo fromSnapshot(DocumentSnapshot snapshot) {
        return snapshot.toObject(Todo.class);
    }
}
