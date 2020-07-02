package vnu.uet.mobilecourse.assistant.database.DAO;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import vnu.uet.mobilecourse.assistant.model.firebase.OnlineState;
import vnu.uet.mobilecourse.assistant.model.firebase.User;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class OnlineStateDAO extends FirebaseDocReadOnlyDAO<OnlineState> {

    private static final CollectionReference COLLECTION_REF = FirebaseFirestore
            .getInstance()
            .collection(FirebaseCollectionName.ONLINE_STATUS);

    public OnlineStateDAO() {
        super(COLLECTION_REF);
    }

    public StateLiveData<String> update(String id, boolean state) {
        // initialize output state live data with loading state
        StateModel<String> loadingState = new StateModel<>(StateStatus.LOADING);
        StateLiveData<String> response = new StateLiveData<>(loadingState);

        // update document into firestore db
        // *note: if this operation executed without internet connection
        // state of the response won't change (still loading)
        mColReference.document(id)
                .update("state", state)
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
    protected OnlineState fromSnapshot(DocumentSnapshot snapshot) {
        return snapshot.toObject(OnlineState.class);
    }
}
