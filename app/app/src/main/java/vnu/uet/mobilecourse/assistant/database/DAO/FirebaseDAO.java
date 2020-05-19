package vnu.uet.mobilecourse.assistant.database.DAO;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import vnu.uet.mobilecourse.assistant.model.FirebaseModel.IFirebaseModel;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public abstract class FirebaseDAO<T extends IFirebaseModel> implements IFirebaseDAO<T> {
    private static final String TAG = FirebaseDAO.class.getSimpleName();

    private static final String OWNER_ID = User.getInstance().getStudentId();

    private String collectionName;

    FirebaseDAO(String collectionName) {
        this.collectionName = collectionName;
    }

    private StateLiveData<List<T>> dataList;

    protected abstract T fromSnapshot(DocumentSnapshot snapshot);

    @Override
    public StateLiveData<List<T>> readAll() {
        if (dataList == null) {
            // initialize with loading state
            dataList = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));

            // listen data from firebase
            FirebaseFirestore.getInstance()
                    // reference to collection
                    .collection(collectionName)
                    // query all document owned by current user
                    .whereEqualTo("ownerId", OWNER_ID)
                    // listen for data change
                    .addSnapshotListener((snapshots, e) -> {
                        if (e != null) {
                            Log.e(TAG, "Listen to data list failed.");
                            dataList.postError(e);

                        } else if (snapshots == null) {
                            Log.d(TAG, "Listening to data list.");
                            dataList.postLoading();

                        } else {
                            List<T> allLists = snapshots.getDocuments().stream()
                                    .map(this::fromSnapshot)
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toList());

                            dataList.postSuccess(allLists);
                        }
                    });
        }

        return dataList;
    }

    @Override
    public StateMediatorLiveData<T> read(String id) {
        readAll();

        StateModel<T> loadingState = new StateModel<>(StateStatus.LOADING);
        StateMediatorLiveData<T> response = new StateMediatorLiveData<>(loadingState);

        response.addSource(dataList, state -> {
            switch (state.getStatus()) {
                case LOADING:
                    response.postLoading();
                    break;

                case ERROR:
                    response.postError(state.getError());
                    break;

                case SUCCESS:
                    T doc = state.getData().stream()
                            .filter(d -> d.getId().equals(id))
                            .findFirst()
                            .orElse(null);

                    if (doc == null)
                        response.postLoading();
                    else
                        response.postSuccess(doc);

                    break;
            }
        });

        return response;
    }

    @Override
    public StateLiveData<T> add(String id, T document) {
        StateModel<T> loadingState = new StateModel<>(StateStatus.LOADING);
        StateLiveData<T> response = new StateLiveData<>(loadingState);

        FirebaseFirestore.getInstance()
                .collection(collectionName)
                .document(id)
                .set(document)
                .addOnSuccessListener(aVoid -> {
                    response.postSuccess(document);
                    Log.d(TAG, "Add a new todo list: " + id);
                })
                .addOnFailureListener(e -> {
                    response.postError(e);
                    Log.e(TAG, "Failed to add todo list: " + id);
                });

        return response;
    }

    @Override
    public StateLiveData<String> delete(String id) {
        StateModel<String> loadingState = new StateModel<>(StateStatus.LOADING);
        StateLiveData<String> response = new StateLiveData<>(loadingState);

        FirebaseFirestore.getInstance()
                .collection(collectionName)
                .document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    response.postSuccess(id);
                    Log.d(TAG, "Delete document: " + id);
                })
                .addOnFailureListener(e -> {
                    response.postError(e);
                    Log.e(TAG, "Failed to delete document: " + id);
                });

        return response;
    }

    @Override
    public StateLiveData<String> update(String id, Map<String, Object> changes) {
        StateModel<String> loadingState = new StateModel<>(StateStatus.LOADING);
        StateLiveData<String> response = new StateLiveData<>(loadingState);

        FirebaseFirestore.getInstance()
                .collection(collectionName)
                .document(id)
                .update(changes)
                .addOnSuccessListener(aVoid -> {
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
