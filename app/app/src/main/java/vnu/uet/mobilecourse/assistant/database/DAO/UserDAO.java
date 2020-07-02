package vnu.uet.mobilecourse.assistant.database.DAO;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.Map;

import vnu.uet.mobilecourse.assistant.model.firebase.MessageToken;
import vnu.uet.mobilecourse.assistant.model.firebase.OnlineState;
import vnu.uet.mobilecourse.assistant.model.firebase.User;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class UserDAO extends FirebaseDocReadOnlyDAO<User> {

    private static final CollectionReference USER_COLLECTION_REF = FirebaseFirestore
            .getInstance()
            .collection(FirebaseCollectionName.USER);

    private static final CollectionReference ONLINE_COLLECTION_REF = FirebaseFirestore
            .getInstance()
            .collection(FirebaseCollectionName.ONLINE_STATUS);

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
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        batch.set(mColReference.document(user.getId()), user);

        // add online state
        OnlineState state = new OnlineState();
        state.setId(user.getId());
        state.setState(false);
        batch.set(ONLINE_COLLECTION_REF.document(user.getId()), state);

        MessageToken token = new MessageToken();
        token.setId(user.getId());
        token.setTimeUpdated(System.currentTimeMillis()/1000);
        token.setToken("");
        batch.set(FirebaseFirestore.getInstance().collection(FirebaseCollectionName.TOKEN).document(user.getId()), token);

        // commit
        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    response.postSuccess(user);
                    Log.d(TAG, "Add a new user: " + user.getId());
                })
                .addOnFailureListener(e -> {
                    response.postError(e);
                    Log.e(TAG, "Failed to add user: " + user.getId());
                });

        return response;
    }

    public StateLiveData<String> update(String id, Map<String, Object> changes) {
        // initialize output state live data with loading state
        StateModel<String> loadingState = new StateModel<>(StateStatus.LOADING);
        StateLiveData<String> response = new StateLiveData<>(loadingState);

        // update document into firestore db
        // *note: if this operation executed without internet connection
        // state of the response won't change (still loading)
        mColReference.document(id)
                .update(changes)
                .addOnSuccessListener(aVoid -> {
                    // response post success with id of updated document
                    response.postSuccess(id);
                    Log.d(TAG, "Change document: " + id);
                })
                .addOnFailureListener(e -> {
                    response.postError(e);
                    Log.e(TAG, "Failed to change document: " + id);
                });

        return response;
    }

    public StateLiveData<String> increaseNotifications(String id) {
        // initialize output state live data with loading state
        StateModel<String> loadingState = new StateModel<>(StateStatus.LOADING);
        StateLiveData<String> response = new StateLiveData<>(loadingState);

        mColReference.document(id)
                .update("newNotifications", FieldValue.increment(1))
                .addOnSuccessListener(aVoid -> {
                    // response post success with id of updated document
                    response.postSuccess(id);
                    Log.d(TAG, "Change document: " + id);
                })
                .addOnFailureListener(e -> {
                    response.postError(e);
                    Log.e(TAG, "Failed to change document: " + id);
                });

        return response;
    }

    @Override
    protected User fromSnapshot(DocumentSnapshot snapshot) {
        return snapshot.toObject(User.class);
    }
}
