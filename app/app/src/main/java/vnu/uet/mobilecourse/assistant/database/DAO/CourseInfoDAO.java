package vnu.uet.mobilecourse.assistant.database.DAO;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import vnu.uet.mobilecourse.assistant.model.firebase.Course;
import vnu.uet.mobilecourse.assistant.model.firebase.FirebaseCollectionName;
import vnu.uet.mobilecourse.assistant.model.firebase.Notification_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.TodoList;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class CourseInfoDAO extends FirebaseDAO<Course> {

    public CourseInfoDAO() {
        super(FirebaseFirestore.getInstance()
                .collection(FirebaseCollectionName.COURSE)
        );
    }

    public StateLiveData<List<Course>> getParticipateCourses(String studentId) {
        StateLiveData<List<Course>> liveData = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));

        // listen data from firebase
        // query all document owned by current user
        FirebaseFirestore.getInstance()
                .collectionGroup(FirebaseCollectionName.PARTICIPANT)
                .whereEqualTo("id", studentId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
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
                            List<Course> courses = new ArrayList<>();

                            for (QueryDocumentSnapshot snapshot : snapshots) {
                                DocumentReference courseDocRef = snapshot.getReference().getParent().getParent();

                                assert courseDocRef != null;
                                courseDocRef.get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot snapshot) {
                                                Course course = snapshot.toObject(Course.class);
                                                courses.add(course);
                                                liveData.postSuccess(courses);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                liveData.postError(e);
                                            }
                                        });
                            };

                        }
                    }
                });

        return liveData;
    }

    @Override
    public StateLiveData<List<Course>> readAll() {
        // this live data will only initialize once
        // data change will auto update by 'addSnapshotListener'
        // to listen for data changes
        if (mDataList == null) {
            // initialize with loading state
            mDataList = getParticipateCourses(STUDENT_ID);
        }

        return mDataList;
    }

    @Deprecated
    @Override
    public StateLiveData<Course> add(String id, Course document) {
        return super.add(id, document);
    }

    @Deprecated
    @Override
    public StateLiveData<String> delete(String id) {
        return super.delete(id);
    }

    @Deprecated
    @Override
    public StateLiveData<String> update(String id, Map<String, Object> changes) {
        return super.update(id, changes);
    }
}
