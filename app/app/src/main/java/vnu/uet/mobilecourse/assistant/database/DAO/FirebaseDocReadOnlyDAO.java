package vnu.uet.mobilecourse.assistant.database.DAO;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import vnu.uet.mobilecourse.assistant.exception.DocumentNotFoundException;
import vnu.uet.mobilecourse.assistant.exception.MultipleDocumentsException;
import vnu.uet.mobilecourse.assistant.model.firebase.IFirebaseModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public abstract class FirebaseDocReadOnlyDAO<T extends IFirebaseModel> implements IFirebaseReadOnlyDAO<T> {
    protected static final String TAG = FirebaseDocReadOnlyDAO.class.getSimpleName();

    private CollectionReference mColReference;
    private String mKeyField;

    /**
     * DAO usually interact in an collection/sub collection
     *
     * @param colRef reference of the corresponding collection
     */
    FirebaseDocReadOnlyDAO(CollectionReference colRef, String keyField) {
        mColReference = colRef;
        mKeyField = keyField;
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

        mColReference.whereEqualTo(mKeyField, id)
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
                        List<T> list = snapshots.getDocuments().stream()
                                .map(this::fromSnapshot)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());

                        if (list.isEmpty()) {
                            liveData.postError(new DocumentNotFoundException());
                        } else if (list.size() == 1) {
                            liveData.postSuccess(list.get(0));
                        } else {
                            liveData.postError(new MultipleDocumentsException());
                        }
                    }
                });

        return liveData;
    }

    protected abstract T fromSnapshot(DocumentSnapshot snapshot);
}
