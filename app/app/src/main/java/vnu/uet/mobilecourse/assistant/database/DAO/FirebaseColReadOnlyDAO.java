package vnu.uet.mobilecourse.assistant.database.DAO;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import vnu.uet.mobilecourse.assistant.exception.DocumentNotFoundException;
import vnu.uet.mobilecourse.assistant.model.firebase.IFirebaseModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public abstract class FirebaseColReadOnlyDAO<T extends List<? extends IFirebaseModel>> implements IFirebaseReadOnlyDAO<T> {
    protected static final String TAG = FirebaseColReadOnlyDAO.class.getSimpleName();

    protected CollectionReference mColReference;
    protected String mCollectionName;

    /**
     * DAO usually interact in an collection/sub collection
     *
     * @param colRef reference of the corresponding collection
     */
    FirebaseColReadOnlyDAO(CollectionReference colRef, String collectionName) {
        mColReference = colRef;
        mCollectionName = collectionName;
    }

    /**
     * Get a document by id
     *
     * This function will filter from mDataList
     * to get the selected document
     *
     * We avoid reading directly from db for reduce read throughput
     *
     * @param id of the document
     *
     * @return state live data contains document
     */
    @Override
    public StateLiveData<T> read(String id) {
        // initialize output state live data with loading state
        StateLiveData<T> liveData = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));

        mColReference
                .document(id)
                .collection(mCollectionName)
                // listen for data change
                .addSnapshotListener((snapshots, e) -> {
                    // catch an exception
                    if (e != null) {
                        Log.e(TAG, "Listen to data list failed.");
                        liveData.postError(e);
                    }
                    // hasn't got snapshots yet
                    else if (snapshots == null) {
                        Log.d(TAG, "Listening to data list.");
                        liveData.postLoading();
                    }
                    // query completed with snapshots
                    else {
                        T list = fromSnapshot(snapshots);

                        if (list.isEmpty()) {
                            handleDocumentNotFound(liveData, id);
                        } else {
                            liveData.postSuccess(list);
                        }
                    }
                });

        return liveData;
    }

    /**
     * Handle document not found after filter read-all result by id
     *
     * @param response live data contains result
     * @param id of needed document
     */
    protected void handleDocumentNotFound(StateLiveData<T> response, String id) {
        response.postError(new DocumentNotFoundException(id));
    }

    protected abstract T fromSnapshot(QuerySnapshot snapshots);
}
