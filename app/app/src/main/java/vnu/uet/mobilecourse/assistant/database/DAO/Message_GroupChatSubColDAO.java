package vnu.uet.mobilecourse.assistant.database.DAO;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import vnu.uet.mobilecourse.assistant.model.event.EventComparator;
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
        mColReference.limit(100).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    messagesState.postError(e);
                } else {
                    List<Message_GroupChatSubCol> messages = queryDocumentSnapshots.getDocuments().stream()
                                                                    .map(doc -> doc.toObject(Message_GroupChatSubCol.class))
                                                                    .filter(Objects::nonNull)
                                                                    .collect(Collectors.toList());
                    messagesState.postSuccess(messages);

                }
            }
        });




        return messagesState;
    }
}
