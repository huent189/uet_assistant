package vnu.uet.mobilecourse.assistant.database.DAO;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import vnu.uet.mobilecourse.assistant.model.firebase.Member_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class Member_GroupChatSubColDAO extends FirebaseDAO<Member_GroupChatSubCol> {
    /**
     * DAO usually interact in an collection/sub collection
     *
     * @param groupId reference of the corresponding collection
     */
    public Member_GroupChatSubColDAO(String groupId) {
        super(FirebaseFirestore.getInstance()
                .collection(FirebaseCollectionName.GROUP_CHAT)
                .document(groupId)
                .collection(FirebaseCollectionName.MEMBER)
        );
    }

    @Override
    public StateLiveData<List<Member_GroupChatSubCol>> readAll() {
        // this live data will only initialize once
        // data change will auto update by 'addSnapshotListener'
        // to listen for data changes
        if (mDataList == null) {
            // initialize with loading state
            mDataList = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));

            // listen data from firebase
            // query all document owned by current user
            mColReference
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
                            List<Member_GroupChatSubCol> members = snapshots.getDocuments().stream()
                                    .map(snapshot -> snapshot.toObject(Member_GroupChatSubCol.class))
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toList());

                            mDataList.postSuccess(members);
                        }
                    });
        }

        return mDataList;
    }
}
