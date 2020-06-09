package vnu.uet.mobilecourse.assistant.database.DAO;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import vnu.uet.mobilecourse.assistant.model.firebase.Notification_UserSubCol;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class NotificationDAO extends FirebaseDAO<Notification_UserSubCol> {

    public NotificationDAO() {
        super(FirebaseFirestore.getInstance()
                .collection(FirebaseCollectionName.USER)
                .document(STUDENT_ID)
                .collection(FirebaseCollectionName.NOTIFICATION)
        );
    }

    @Override
    public StateLiveData<List<Notification_UserSubCol>> readAll() {
        // this live data will only initialize once
        // data change will auto update by 'addSnapshotListener'
        // to listen for data changes
        if (mDataList == null) {
            // initialize with loading state
            mDataList = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));

            // listen data from firebase
            // query all document owned by current user
            mColReference
                    .orderBy("notifyTime", Query.Direction.DESCENDING)
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
//                            List<Notification_UserSubCol> allLists = snapshots.getDocuments().stream()
//                                    .map(snapshot -> snapshot.toObject(Notification_UserSubCol.class))
//                                    .filter(Objects::nonNull)
//                                    .collect(Collectors.toList());

//                            mDataList.postSuccess(allLists);
                        }
                    });
        }

        return mDataList;
    }
}
