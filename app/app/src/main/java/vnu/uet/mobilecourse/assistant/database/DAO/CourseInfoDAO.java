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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import vnu.uet.mobilecourse.assistant.model.Course;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseInfo;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseSession;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class CourseInfoDAO extends FirebaseDAO<CourseInfo> {

    public CourseInfoDAO() {
        super(FirebaseFirestore.getInstance()
                .collection(FirebaseCollectionName.COURSE)
        );
    }

    public StateLiveData<List<CourseInfo>> getParticipateCourses(String studentId) {
        StateLiveData<List<CourseInfo>> liveData = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));

        // listen data from firebase
        // query all document owned by current user
        FirebaseFirestore.getInstance()
                .collectionGroup(FirebaseCollectionName.PARTICIPANT)
                .whereEqualTo("studentId", studentId)
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
                            List<CourseInfo> courses = new ArrayList<>();

                            for (QueryDocumentSnapshot snapshot : snapshots) {
                                DocumentReference courseDocRef = snapshot.getReference().getParent().getParent();

                                assert courseDocRef != null;
                                courseDocRef.get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot snapshot) {
                                                CourseInfo course = fromSnapshot(snapshot);
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
                            }

                        }
                    }
                });

        return liveData;
    }

    @Override
    public StateLiveData<List<CourseInfo>> readAll() {
        // this live data will only initialize once
        // data change will auto update by 'addSnapshotListener'
        // to listen for data changes
        if (mDataList == null) {
            // initialize with loading state
            mDataList = getParticipateCourses(STUDENT_ID);
        }

        return mDataList;
    }

    private CourseInfo fromSnapshot(DocumentSnapshot snapshot) {
        CourseInfo courseInfo = snapshot.toObject(CourseInfo.class);

        // convert sessions map to list of session
        Object o1 = snapshot.get("courseInfo");
        if (courseInfo != null && o1 instanceof Map) {

            for (Object o2 : ((Map) o1).entrySet()) {
                CourseSession session = new CourseSession();

                session.setCourseName(courseInfo.getName());

                Map.Entry entry = (Map.Entry) o2;

                String key = entry.getKey().toString();

                if (key.matches("[0-9] \\| [0-9]{1,2}-[0-9]{1,2}")) {
                    String[] temp = key.split("\\Q | \\E");

                    session.setDayOfWeek(Integer.parseInt(temp[0]));

                    temp = temp[1].split("-");

                    session.setStart(Integer.parseInt(temp[0]));
                    session.setEnd(Integer.parseInt(temp[1]));
                }

                Object value = entry.getValue();

                if (value instanceof Map) {
                    Map map = (Map) value;

                    Object teacherName = map.get("teacherName");
                    if (teacherName != null) {
                        session.setTeacherName(teacherName.toString());
                    }

                    Object classroom = map.get("classroom");
                    if (classroom != null) {
                        session.setClassroom(classroom.toString());
                    }

                    Object type = map.get("type");
                    if (type != null) {
                        switch (type.toString().toUpperCase()) {
                            case "CL":
                                session.setType(0);
                                break;

                            case "N1":
                                session.setType(1);
                                break;

                            case "N2":
                                session.setType(2);
                                break;

                            case "N3":
                                session.setType(3);
                                break;
                        }
                    }

                }

                courseInfo.getSessions().add(session);

            }
        }


        return courseInfo;
    }

    @Deprecated
    @Override
    public StateLiveData<CourseInfo> add(String id, CourseInfo document) {
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
