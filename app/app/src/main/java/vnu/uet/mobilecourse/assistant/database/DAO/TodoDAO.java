package vnu.uet.mobilecourse.assistant.database.DAO;

import com.google.firebase.firestore.DocumentSnapshot;

import vnu.uet.mobilecourse.assistant.model.FirebaseModel.FirebaseCollectionName;
import vnu.uet.mobilecourse.assistant.model.FirebaseModel.Todo;

public class TodoDAO extends FirebaseDAO<Todo> {

    public TodoDAO() {
        super(FirebaseCollectionName.TODO);
    }

    @Override
    protected Todo fromSnapshot(DocumentSnapshot snapshot) {
        return snapshot.toObject(Todo.class);
    }

    @Override
    protected boolean filterById(String id, Todo document) {
        return document.getTodoId().equals(id);
    }
}
