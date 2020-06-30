package vnu.uet.mobilecourse.assistant.database.DAO;

import android.util.Log;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import vnu.uet.mobilecourse.assistant.exception.DocumentNotFoundException;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.IFirebaseModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

import java.util.List;
import java.util.Map;

public abstract class FirebaseDAO<T extends IFirebaseModel> implements IFirebaseDAO<T> {
    protected static final String TAG = FirebaseDAO.class.getSimpleName();
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    /**
     * Student Id = Owner Id
     */
    protected static final String STUDENT_ID = User.getInstance().getStudentId();

    protected CollectionReference mColReference;

    /**
     * DAO usually interact in an collection/sub collection
     *
     * @param colRef reference of the corresponding collection
     */
    FirebaseDAO(CollectionReference colRef) {
        mColReference = colRef;
    }

    /**
     * state live data contains a list of model
     * this will initialize once,
     * and we will listen for snapshot change
     * to update live data.
     */
    protected StateLiveData<List<T>> mDataList;

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
    public StateMediatorLiveData<T> read(String id) {
        // query to get all documents
        // in case list live data hasn't init yet
        readAll();

        // initialize output state live data with loading state
        StateModel<T> loadingState = new StateModel<>(StateStatus.LOADING);
        StateMediatorLiveData<T> response = new StateMediatorLiveData<>();
        response.postLoading();

        if (mDataList.getValue() != null && mDataList.getValue().getData() != null) {
            // filter the selected document by id
            T doc = mDataList.getValue().getData().stream()
                    .filter(d -> d.getId().equals(id))
                    .findFirst()
                    .orElse(null);

            // in case can't not found document
            // we will post loading state
            // (or error state .-. idk)
            if (doc == null) handleDocumentNotFound(response, id);
                // post success state when find the doc
            else response.postSuccess(doc);
        }

        // output state live data will bind with mDataList
        // when mDataList change, output from this function
        // will auto update data
        response.addSource(mDataList, state -> {
            // consider the state of mDataList
            switch (state.getStatus()) {
                // post loading state
                case LOADING:
                    response.postLoading();
                    break;

                // post error state
                case ERROR:
                    response.postError(state.getError());
                    break;

                // post success state
                case SUCCESS:
                    // filter the selected document by id
                    T doc = state.getData().stream()
                            .filter(d -> d.getId().equals(id))
                            .findFirst()
                            .orElse(null);

                    // in case can't not found document
                    // we will post loading state
                    // (or error state .-. idk)
                    if (doc == null) handleDocumentNotFound(response, id);
                    // post success state when find the doc
                    else response.postSuccess(doc);

                    break;
            }
        });

        return response;
    }

    /**
     * Handle document not found after filter read-all result by id
     *
     * @param response live data contains result
     * @param id of needed document
     */
    protected void handleDocumentNotFound(StateMediatorLiveData<T> response, String id) {
        response.postError(new DocumentNotFoundException(id));
    }

    /**
     * Add a document into firestore db
     *
     * @param id of document
     * @param document contains document info
     *
     * @return state live data contains
     * response of this function
     */
    @Override
    public StateLiveData<T> add(String id, T document) {
        // initialize output state live data with loading state
        StateModel<T> loadingState = new StateModel<>(StateStatus.LOADING);
        StateLiveData<T> response = new StateLiveData<>(loadingState);

        // add document into firestore db
        // *note: if this operation executed without internet connection
        // state of the response won't change (still loading)
        mColReference.document(id)
                .set(document)
                .addOnSuccessListener(aVoid -> {
                    response.postSuccess(document);
                    Log.d(TAG, "Add a new document: " + id);
                })
                .addOnFailureListener(e -> {
                    response.postError(e);
                    Log.e(TAG, "Failed to add document: " + id);
                });

        return response;
    }

    /**
     * Delete a document by id
     *
     * @param id of the document
     *
     * @return state live data contains
     * response of this function
     */
    @Override
    public StateLiveData<String> delete(String id) {
        // initialize output state live data with loading state
        StateModel<String> loadingState = new StateModel<>(StateStatus.LOADING);
        StateLiveData<String> response = new StateLiveData<>(loadingState);

        // delete document from firestore db
        // *note: if this operation executed without internet connection
        // state of the response won't change (still loading)
        mColReference.document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // response post success with id of deleted document
                    response.postSuccess(id);
                    Log.d(TAG, "Delete document: " + id);
                })
                .addOnFailureListener(e -> {
                    response.postError(e);
                    Log.e(TAG, "Failed to delete document: " + id);
                });

        return response;
    }

    /**
     * Update a document by id
     *
     * @param id of the document
     * @param changes - a field name to value map
     *
     * @return state live data contains
     * response of this function
     */
    @Override
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
}
