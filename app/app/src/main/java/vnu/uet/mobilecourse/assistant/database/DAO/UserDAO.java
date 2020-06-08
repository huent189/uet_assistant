package vnu.uet.mobilecourse.assistant.database.DAO;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import vnu.uet.mobilecourse.assistant.model.firebase.User;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class UserDAO extends FirebaseDocReadOnlyDAO<User> {

    private static final CollectionReference USER_COLLECTION_REF = FirebaseFirestore
            .getInstance()
            .collection(FirebaseCollectionName.USER);

    public UserDAO() {
        super(USER_COLLECTION_REF);
    }

    public IStateLiveData<User> add(User user) {
        // initialize output state live data with loading state
        StateModel<User> loadingState = new StateModel<>(StateStatus.LOADING);
        StateLiveData<User> response = new StateLiveData<>(loadingState);

        // add document into firestore db
        // *note: if this operation executed without internet connection
        // state of the response won't change (still loading)
        mColReference.document(user.getId())
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    response.postSuccess(user);
                    Log.d(TAG, "Add a new todo list: " + user.getId());
                })
                .addOnFailureListener(e -> {
                    response.postError(e);
                    Log.e(TAG, "Failed to add todo list: " + user.getId());
                });

        return response;
    }

    @Override
    protected User fromSnapshot(DocumentSnapshot snapshot) {
        return snapshot.toObject(User.class);
    }
}
