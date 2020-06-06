package vnu.uet.mobilecourse.assistant.database.DAO;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import vnu.uet.mobilecourse.assistant.model.firebase.UserInfo;

public class UserInfoDAO extends FirebaseDocReadOnlyDAO<UserInfo> {

    private static final CollectionReference USER_INFO_COLLECTION_REF = FirebaseFirestore
            .getInstance()
            .collection(FirebaseCollectionName.USER_INFO);

    public UserInfoDAO() {
            super(USER_INFO_COLLECTION_REF);
    }

    @Override
    protected UserInfo fromSnapshot(DocumentSnapshot snapshot) {
        return snapshot.toObject(UserInfo.class);
    }
}
