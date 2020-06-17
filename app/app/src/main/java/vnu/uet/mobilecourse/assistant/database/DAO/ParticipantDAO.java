package vnu.uet.mobilecourse.assistant.database.DAO;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.stream.Collectors;

import vnu.uet.mobilecourse.assistant.model.firebase.Participant_CourseSubCol;
import vnu.uet.mobilecourse.assistant.util.StringConst;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;

import static vnu.uet.mobilecourse.assistant.util.FbAndCourseMap.DELIMITER_POS;

public class ParticipantDAO extends FirebaseColReadOnlyDAO<List<Participant_CourseSubCol>> {

    public ParticipantDAO() {
        super(FirebaseFirestore.getInstance().collection(FirebaseCollectionName.COURSE), FirebaseCollectionName.PARTICIPANT);
    }

    @Override
    protected void handleDocumentNotFound(StateLiveData<List<Participant_CourseSubCol>> liveData, String id) {
        StringBuilder builder = new StringBuilder(id);

        if (builder.charAt(DELIMITER_POS) == StringConst.SPACE_CHAR) {
            builder.deleteCharAt(DELIMITER_POS);
        } else {
            builder.insert(DELIMITER_POS, StringConst.SPACE_CHAR);
        }

        String code = builder.toString();

        mColReference
                .document(code)
                .collection(mCollectionName)
                // listen for data change
                .addSnapshotListener((snapshots, e) -> {
                    // catch an exception
                    if (e != null) {
                        Log.e(TAG, "Listen to data list failed.");
                        liveData.postError(e);
                    }
                    // hasn't got snapshots yet
                    else if (snapshots == null) {
                        Log.d(TAG, "Listening to data list.");
                        liveData.postLoading();
                    }
                    // query completed with snapshots
                    else {
                        List<Participant_CourseSubCol> list = fromSnapshot(snapshots);
                        liveData.postSuccess(list);
                    }
                });
    }

    @Override
    protected List<Participant_CourseSubCol> fromSnapshot(QuerySnapshot snapshots) {
        return snapshots.getDocuments().stream()
                .map(snapshot -> snapshot.toObject(Participant_CourseSubCol.class))
                .collect(Collectors.toList());
    }
}
