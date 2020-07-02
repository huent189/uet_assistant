package vnu.uet.mobilecourse.assistant.database.DAO;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.model.firebase.Member_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.MessageToken;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class GroupChatDAO extends FirebaseDocReadOnlyDAO<GroupChat> {

    public GroupChatDAO() {
        super(FirebaseFirestore.getInstance().collection(FirebaseCollectionName.GROUP_CHAT));
    }

//    /**
//     * Avoid query all group chat in db
//     */
//    @Deprecated
//    @Override
//    public StateLiveData<List<GroupChat>> readAll() {
//        if (mDataList == null) {
//
//            mDataList = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));
//
//            // listen data from firebase
//            // query all document owned by current user
//            FirebaseFirestore.getInstance()
//                    .collectionGroup(FirebaseCollectionName.MEMBER)
//                    .whereEqualTo("id", STUDENT_ID)
//                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                        @Override
//                        public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
//                            // catch an exception
//                            if (e != null) {
//                                Log.e(TAG, "Listen to data list failed.");
//                                mDataList.postError(e);
//                            }
//                            // hasn't got snapshots yet
//                            else if (snapshots == null) {
//                                Log.d(TAG, "Listening to data list.");
//                                mDataList.postLoading();
//                            }
//                            // query completed with snapshots
//                            else {
//                                List<GroupChat> groupChats = new ArrayList<>();
//
//                                for (QueryDocumentSnapshot snapshot : snapshots) {
//                                    DocumentReference groupChatDocRef = snapshot.getReference().getParent().getParent();
//
//                                    assert groupChatDocRef != null;
//                                    groupChatDocRef.get()
//                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                                @Override
//                                                public void onSuccess(DocumentSnapshot snapshot) {
//                                                    GroupChat groupChat = snapshot.toObject(GroupChat.class);
//                                                    if (groupChat != null && !groupChats.contains(groupChat)) {
//                                                        groupChats.add(groupChat);
//                                                        mDataList.postSuccess(groupChats);
//                                                    }
//                                                }
//                                            })
//                                            .addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception e) {
//                                                    mDataList.postError(e);
//                                                }
//                                            });
//                                }
//                                ;
//
//                            }
//                        }
//                    });
//        }
//
//        return mDataList;
//    }

    public StateLiveData<GroupChat> add(String id, GroupChat document) {
        // initialize output state live data with loading state
        StateModel<GroupChat> loadingState = new StateModel<>(StateStatus.LOADING);
        StateLiveData<GroupChat> response = new StateLiveData<>(loadingState);

        // add document into firestore db
        // *note: if this operation executed without internet connection
        // state of the response won't change (still loading)
        mColReference.document(id)
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


    public StateLiveData<String> addMembers(String groupId, List<Member_GroupChatSubCol> members) {
        StateLiveData<String> addMemberState = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));

        // add member to group chat
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        for (Member_GroupChatSubCol member: members) {
            DocumentReference memberRef = mColReference.document(groupId)
                                            .collection(FirebaseCollectionName.MEMBER)
                                            .document(member.getId());
            batch.set(memberRef, member);
        }

        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                for (Member_GroupChatSubCol member:
                        members) {
                    IStateLiveData<MessageToken> liveData = new TokenDAO().read(member.getId());
                    liveData.observeForever(new Observer<StateModel<MessageToken>>() {
                        @Override
                        public void onChanged(StateModel<MessageToken> stateModel) {
                            switch (stateModel.getStatus()) {
                                case SUCCESS:
                                    liveData.removeObserver(this);
                                    FirebaseFirestore.getInstance().collection(FirebaseCollectionName.GROUP_CHAT).document(groupId).collection(FirebaseCollectionName.MEMBER).document(member.getId()).update("token", stateModel.getData().getToken());
                            }
                        }
                    });
                }
                addMemberState.postSuccess("add member success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                addMemberState.postError(e);
            }
        });
        return addMemberState;
    }

    @Override
    protected GroupChat fromSnapshot(DocumentSnapshot snapshot) {
        return snapshot.toObject(GroupChat.class);
    }
}
