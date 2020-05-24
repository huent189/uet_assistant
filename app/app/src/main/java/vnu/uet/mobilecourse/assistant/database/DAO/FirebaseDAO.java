package vnu.uet.mobilecourse.assistant.database.DAO;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import vnu.uet.mobilecourse.assistant.model.firebase.IFirebaseModel;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public abstract class FirebaseDAO<T extends IFirebaseModel> implements IFirebaseDAO<T> {
    private static final String TAG = FirebaseDAO.class.getSimpleName();

    protected static final String OWNER_ID = User.getInstance().getStudentId();

    private CollectionReference mColReference;

    FirebaseDAO(CollectionReference colRef) {
        mColReference = colRef;
    }

    private StateLiveData<List<T>> mDataList;

    protected abstract T fromSnapshot(DocumentSnapshot snapshot);

    @Override
    public StateLiveData<List<T>> readAll() {
        if (mDataList == null) {
            // initialize with loading state
            mDataList = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));

            // listen data from firebase
            // query all document owned by current user
            mColReference.whereEqualTo("ownerId", OWNER_ID)
                    // listen for data change
                    .addSnapshotListener((snapshots, e) -> {
                        if (e != null) {
                            Log.e(TAG, "Listen to data list failed.");
                            mDataList.postError(e);

                        } else if (snapshots == null) {
                            Log.d(TAG, "Listening to data list.");
                            mDataList.postLoading();

                        } else {
                            List<T> allLists = snapshots.getDocuments().stream()
                                    .map(this::fromSnapshot)
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toList());

                            mDataList.postSuccess(allLists);
                        }
                    });
        }
        return mDataList;
    }

    @Override
    public StateMediatorLiveData<T> read(String id) {
        readAll();

        StateModel<T> loadingState = new StateModel<>(StateStatus.LOADING);
        StateMediatorLiveData<T> response = new StateMediatorLiveData<>(loadingState);

        response.addSource(mDataList, state -> {
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


        mColReference.document(id)
                .set(document)
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        System.out.println();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        System.out.println();
                    }
                })
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

        mColReference.document(id)
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

        mColReference.document(id)
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
