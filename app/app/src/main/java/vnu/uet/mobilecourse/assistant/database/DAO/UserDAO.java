package vnu.uet.mobilecourse.assistant.database.DAO;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import vnu.uet.mobilecourse.assistant.model.firebase.User;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class UserDAO {

    private static final String TAG = User.class.getName();

    private CollectionReference mColReference = FirebaseFirestore.getInstance()
            .collection(FirebaseCollectionName.USER);

    public StateLiveData<User> search(String id) {
        StateLiveData<User> liveData = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));

        mColReference.whereEqualTo("id", id)
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
                        List<User> list = snapshots.getDocuments().stream()
                                .map(snapshot -> snapshot.toObject(User.class))
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());

                        if (list.isEmpty()) {
                            liveData.postLoading();
                        } else {
                            liveData.postSuccess(list.get(0));
                        }
                    }
                });

        return liveData;
    }
}
