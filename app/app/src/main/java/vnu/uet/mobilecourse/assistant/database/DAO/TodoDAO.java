package vnu.uet.mobilecourse.assistant.database.DAO;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import vnu.uet.mobilecourse.assistant.model.event.EventComparator;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class TodoDAO extends FirebaseDAO<Todo> {

    public TodoDAO() {
        super(FirebaseFirestore.getInstance().collection(FirebaseCollectionName.TODO));
    }

    @Override
    public StateLiveData<List<Todo>> readAll() {
        // this live data will only initialize once
        // data change will auto update by 'addSnapshotListener'
        // to listen for data changes
        if (mDataList == null) {
            // initialize with loading state
            mDataList = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));

            // listen data from firebase
            // query all document owned by current user
            mColReference.whereEqualTo("ownerId", STUDENT_ID)
                    // order
//                    .orderBy("deadline", Query.Direction.DESCENDING)
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
                            List<Todo> allLists = snapshots.getDocuments().stream()
                                    .map(snapshot -> snapshot.toObject(Todo.class))
                                    .filter(Objects::nonNull)
                                    .sorted(new EventComparator())
                                    .collect(Collectors.toList());

                            mDataList.postSuccess(allLists);
                        }
                    });
        }

        return mDataList;
    }
}
