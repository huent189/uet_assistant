package vnu.uet.mobilecourse.assistant.database.DAO;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.stream.Collectors;

import vnu.uet.mobilecourse.assistant.model.firebase.Participant_CourseSubCol;

public class ParticipantDAO extends FirebaseColReadOnlyDAO<List<Participant_CourseSubCol>> {

    public ParticipantDAO() {
        super(FirebaseFirestore.getInstance().collection(FirebaseCollectionName.COURSE));
    }

    @Override
    protected List<Participant_CourseSubCol> fromSnapshot(QuerySnapshot snapshots) {
        return snapshots.getDocuments().stream()
                .map(snapshot -> snapshot.toObject(Participant_CourseSubCol.class))
                .collect(Collectors.toList());
    }
}
