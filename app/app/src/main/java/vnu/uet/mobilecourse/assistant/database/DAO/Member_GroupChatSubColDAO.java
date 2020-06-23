package vnu.uet.mobilecourse.assistant.database.DAO;

import com.google.firebase.firestore.CollectionReference;
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
        super(FirebaseFirestore.getInstance().collection(FirebaseCollectionName.GROUP_CHAT).document(groupId).collection(FirebaseCollectionName.MEMBER));
    }

    @Override
    public StateLiveData<List<Member_GroupChatSubCol>> readAll() {
        StateLiveData<List<Member_GroupChatSubCol>> readState = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));
        mColReference.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Member_GroupChatSubCol> members = queryDocumentSnapshots.getDocuments().stream()
                                                                            .map(documentSnapshot -> documentSnapshot.toObject(Member_GroupChatSubCol.class))
                                                                            .filter(Objects::nonNull)
                                                                            .collect(Collectors.toList());
            readState.postSuccess(members);
        }).addOnFailureListener(e -> {
            readState.postError(e);
        });

        return readState;
    }
}
