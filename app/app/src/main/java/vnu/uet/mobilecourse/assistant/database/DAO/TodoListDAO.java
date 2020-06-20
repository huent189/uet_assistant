package vnu.uet.mobilecourse.assistant.database.DAO;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import vnu.uet.mobilecourse.assistant.model.firebase.TodoList;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class TodoListDAO extends FirebaseDAO<TodoList> {

    private CollectionReference mTodoColReference;

    public TodoListDAO() {
        super(FirebaseFirestore.getInstance().collection(FirebaseCollectionName.TODO_LIST));
        mTodoColReference = FirebaseFirestore.getInstance().collection(FirebaseCollectionName.TODO);
    }

    @Override
    public StateLiveData<List<TodoList>> readAll() {
        // this live data will only initialize once
        // data change will auto update by 'addSnapshotListener'
        // to listen for data changes
        if (mDataList == null) {
            // initialize with loading state
            mDataList = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));

            // listen data from firebase
            // query all document owned by current user
            mColReference.whereEqualTo("ownerId", STUDENT_ID)
                    // listen for data change
                    .addSnapshotListener((snapshots, e) -> {
                        // catch an exception
                        if (e != null) {
                            Log.e(TAG, "Listen to data list failed.");
                            mDataList.postError(e);
                        }
                        // hasn't got snapshots yet
                        else if (snapshots == null) {
                            Log.d(TAG, "Listening to data list.");
                            mDataList.postLoading();
                        }
                        // query completed with snapshots
                        else {
                            List<TodoList> allLists = snapshots.getDocuments().stream()
                                    .map(snapshot -> snapshot.toObject(TodoList.class))
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toList());

                            mDataList.postSuccess(allLists);
                        }
                    });
        }

        return mDataList;
    }

    @Deprecated
    @Override
    public StateLiveData<String> delete(String id) {
        return super.delete(id);
    }

    public StateLiveData<String> deleteDeep(String id, String[] todoIds) {
        // initialize output state live data with loading state
        StateModel<String> loadingState = new StateModel<>(StateStatus.LOADING);
        StateLiveData<String> response = new StateLiveData<>(loadingState);

        // initialize write batch
        WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();

        writeBatch.delete(mColReference.document(id));

        for (String todoId : todoIds) {
            writeBatch.delete(mTodoColReference.document(todoId));
        }

        // commit
        writeBatch.commit()
                .addOnSuccessListener(aVoid -> {
                    response.postSuccess(id);
                    Log.d(TAG, "Delete todo list with its todo: " + id);
                })
                .addOnFailureListener(e -> {
                    response.postError(e);
                    Log.e(TAG, "Failed to delete todo list: " + id);
                });

        return response;
    }
}
