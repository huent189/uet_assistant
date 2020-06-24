package vnu.uet.mobilecourse.assistant.database.DAO;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import vnu.uet.mobilecourse.assistant.model.forum.InterestedDiscussion;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class InterestedDiscussionDAO extends FirebaseDAO<InterestedDiscussion> {

    public InterestedDiscussionDAO() {
        super(FirebaseFirestore.getInstance()
                .collection(FirebaseCollectionName.USER)
                .document(STUDENT_ID)
                .collection(FirebaseCollectionName.INTERESTED_DISCUSSION)
        );
    }

    public synchronized List<InterestedDiscussion> getAllSynchronize() throws ExecutionException, InterruptedException {
        Task<QuerySnapshot> task = mColReference.get();
        Tasks.await(task);
        return task.getResult().getDocuments().stream()
                .map(snapshot -> snapshot.toObject(InterestedDiscussion.class))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public StateLiveData<List<InterestedDiscussion>> readAll() {
        // this live data will only initialize once
        // data change will auto update by 'addSnapshotListener'
        // to listen for data changes
        if (mDataList == null) {
            // initialize with loading state
            mDataList = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));

            // listen data from firebase
            // query all document owned by current user
            mColReference
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
                            List<InterestedDiscussion> allLists = snapshots.getDocuments().stream()
                                    .map(snapshot -> snapshot.toObject(InterestedDiscussion.class))
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toList());

                            mDataList.postSuccess(allLists);
                        }
                    });
        }

        return mDataList;
    }
}
