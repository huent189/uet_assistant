package vnu.uet.mobilecourse.assistant.database.DAO;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MediatorLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import vnu.uet.mobilecourse.assistant.model.event.EventComparator;
import vnu.uet.mobilecourse.assistant.model.firebase.Member_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.Message_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class Message_GroupChatSubColDAO extends FirebaseDAO<Message_GroupChatSubCol> {

    /**
     * DAO usually interact in an collection/sub collection
     *
     * @param groupChatId to get document that contains message
     */

    public Message_GroupChatSubColDAO(String groupChatId) {
        super(FirebaseFirestore.getInstance() // db
                                .collection(FirebaseCollectionName.GROUP_CHAT) // Group Chat Col
                                .document(groupChatId)
                                .collection(FirebaseCollectionName.MESSAGE)
        );
    }

    @Override
    public StateLiveData<List<Message_GroupChatSubCol>> readAll() {
        StateLiveData<List<Message_GroupChatSubCol>> messagesState = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));

        mColReference.orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        // catch an exception
                        if (e != null) {
                            messagesState.postError(e);
                        }
                        // hasn't got snapshots yet
                        else if (snapshots == null) {
                            Log.d(TAG, "Listening to data list.");
                            mDataList.postLoading();
                        }
                        // query completed with snapshots
                        else {
                            List<Message_GroupChatSubCol> messages;
                            messages = snapshots.getDocuments().stream()
                                    .map(doc -> fromSnapshot(doc))
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toList());

                            messagesState.postSuccess(messages);

                        }
                    }
                });

        return messagesState;
    }

    private Message_GroupChatSubCol fromSnapshot(DocumentSnapshot snapshot) {
        Message_GroupChatSubCol message = snapshot.toObject(Message_GroupChatSubCol.class);
        return message;
    }
}
