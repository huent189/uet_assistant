package vnu.uet.mobilecourse.assistant.database.DAO;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import vnu.uet.mobilecourse.assistant.model.firebase.FirebaseCollectionName;
import vnu.uet.mobilecourse.assistant.model.firebase.Notification_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;

public class NotificationDAO extends FirebaseDAO<Notification_UserSubCol> {

    public NotificationDAO() {
        super(FirebaseFirestore.getInstance()
                .collection(FirebaseCollectionName.USER)
                .document(OWNER_ID)
                .collection(FirebaseCollectionName.NOTIFICATION)
        );
    }

    @Override
    protected Notification_UserSubCol fromSnapshot(DocumentSnapshot snapshot) {
        return snapshot.toObject(Notification_UserSubCol.class);
    }
}
