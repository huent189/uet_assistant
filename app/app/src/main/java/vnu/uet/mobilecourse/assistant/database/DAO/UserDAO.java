package vnu.uet.mobilecourse.assistant.database.DAO;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import vnu.uet.mobilecourse.assistant.model.firebase.User;

public class UserDAO extends FirebaseDocReadOnlyDAO<User> {

    private static final String USER_ID_FIELD_NAME = "id";

    private static final CollectionReference USER_COLLECTION_REF = FirebaseFirestore
            .getInstance()
            .collection(FirebaseCollectionName.USER);


    public UserDAO() {
        super(USER_COLLECTION_REF, USER_ID_FIELD_NAME);
    }

    @Override
    protected User fromSnapshot(DocumentSnapshot snapshot) {
        return snapshot.toObject(User.class);
    }
}
