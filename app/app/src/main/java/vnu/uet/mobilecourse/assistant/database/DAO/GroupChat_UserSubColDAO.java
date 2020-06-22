package vnu.uet.mobilecourse.assistant.database.DAO;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.Member_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class GroupChat_UserSubColDAO extends FirebaseDAO<GroupChat_UserSubCol> {

    public GroupChat_UserSubColDAO() {
        super(FirebaseFirestore.getInstance()
                .collection(FirebaseCollectionName.USER)
                .document(STUDENT_ID)
                .collection(FirebaseCollectionName.GROUP_CHAT)
        );
    }

    @Override
    public StateLiveData<List<GroupChat_UserSubCol>> readAll() {
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
                            List<GroupChat_UserSubCol> list = snapshots.getDocuments().stream()
                                    .map(snapshot -> snapshot.toObject(GroupChat_UserSubCol.class))
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toList());

                            mDataList.postSuccess(list);
                        }
                    });
        }

        return mDataList;
    }

    public StateLiveData<String> addGroupChat(GroupChat groupChat) {
        StateLiveData<String> addGroupChatState = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));
        GroupChat_UserSubCol groupChat_userSubCol = new GroupChat_UserSubCol();
        groupChat_userSubCol.setSeen(false);
        groupChat_userSubCol.setAvatar(groupChat.getAvatar());
        groupChat_userSubCol.setName(groupChat.getName());
        groupChat_userSubCol.setId(groupChat.getId());

        WriteBatch batch = db.batch();
        for (Member_GroupChatSubCol member :
                groupChat.getMembers()) {
            DocumentReference groupChatDocRef = db
                    .collection(FirebaseCollectionName.USER)
                    .document(member.getId())
                    .collection(FirebaseCollectionName.GROUP_CHAT)
                    .document(groupChat.getId());
            batch.set(groupChatDocRef, groupChat_userSubCol);
        }
        batch.commit().addOnFailureListener(addGroupChatState::postError)
                .addOnSuccessListener((Void)->
                    addGroupChatState.postSuccess("add group chat success")
                );

        return addGroupChatState;
    }
}
