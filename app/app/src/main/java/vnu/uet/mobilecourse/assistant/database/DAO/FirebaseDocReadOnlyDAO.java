package vnu.uet.mobilecourse.assistant.database.DAO;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import vnu.uet.mobilecourse.assistant.exception.DocumentNotFoundException;
import vnu.uet.mobilecourse.assistant.model.firebase.IFirebaseModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public abstract class FirebaseDocReadOnlyDAO<T extends IFirebaseModel> implements IFirebaseReadOnlyDAO<T> {
    protected static final String TAG = FirebaseDocReadOnlyDAO.class.getSimpleName();

    protected CollectionReference mColReference;

    /**
     * DAO usually interact in an collection/sub collection
     *
     * @param colRef reference of the corresponding collection
     */
    FirebaseDocReadOnlyDAO(CollectionReference colRef) {
        mColReference = colRef;
    }

    /**
     * Get a document by id
     *
     * @param id of the document
     * @return state live data contains document
     */
    @Override
    public StateLiveData<T> read(String id) {
        // initialize output state live data with loading state
        StateLiveData<T> liveData = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));

        mColReference.document(id)
                // listen for data change
                .addSnapshotListener((snapshot, e) -> {
                    // catch an exception
                    if (e != null) {
                        Log.e(TAG, "Listen to data list failed.");
                        liveData.postError(e);
                    }
                    // hasn't got snapshots yet
                    else if (snapshot == null) {
                        Log.d(TAG, "Listening to data list.");
                        liveData.postError(new DocumentNotFoundException(id));
                    }
                    // query completed with snapshots
                    else {
                        T result = fromSnapshot(snapshot);

                        if (result == null) {
                            liveData.postError(new DocumentNotFoundException(id));
                        } else {
                            liveData.postSuccess(result);
                        }
                    }
                });

        return liveData;
    }

    protected abstract T fromSnapshot(DocumentSnapshot snapshot);
}
