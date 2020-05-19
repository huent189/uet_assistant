package vnu.uet.mobilecourse.assistant.database.DAO;

import com.google.firebase.firestore.DocumentSnapshot;

import vnu.uet.mobilecourse.assistant.model.FirebaseModel.FirebaseCollectionName;
import vnu.uet.mobilecourse.assistant.model.FirebaseModel.TodoList;

public class TodoListDAO extends FirebaseDAO<TodoList> {
    public TodoListDAO() {
        super(FirebaseCollectionName.TODO_LIST);
    }

    @Override
    protected TodoList fromSnapshot(DocumentSnapshot snapshot) {
        return snapshot.toObject(TodoList.class);
    }

    @Override
    protected boolean filterById(String id, TodoList document) {
        return document.getTodoListId().equals(id);
    }
}
