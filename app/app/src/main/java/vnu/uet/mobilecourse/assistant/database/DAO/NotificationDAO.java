package vnu.uet.mobilecourse.assistant.database.DAO;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import vnu.uet.mobilecourse.assistant.model.firebase.notification.AdminNotification;
import vnu.uet.mobilecourse.assistant.model.firebase.notification.CourseAttendantNotification;
import vnu.uet.mobilecourse.assistant.model.firebase.notification.NewMaterialNotification;
import vnu.uet.mobilecourse.assistant.model.firebase.NotificationType;
import vnu.uet.mobilecourse.assistant.model.firebase.notification.Notification_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.notification.TodoNotification;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class NotificationDAO extends FirebaseDAO<Notification_UserSubCol> {

    public NotificationDAO() {
        super(FirebaseFirestore.getInstance()
                .collection(FirebaseCollectionName.USER)
                .document(STUDENT_ID)
                .collection(FirebaseCollectionName.NOTIFICATION)
        );
    }

    @Override
    public StateLiveData<List<Notification_UserSubCol>> readAll() {
        // this live data will only initialize once
        // data change will auto update by 'addSnapshotListener'
        // to listen for data changes
        if (mDataList == null) {
            // initialize with loading state
            mDataList = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));

            // listen data from firebase
            // query all document owned by current user
            mColReference
                    .orderBy("notifyTime", Query.Direction.DESCENDING)
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
                            List<Notification_UserSubCol> allLists = snapshots.getDocuments().stream()
                                    .map(this::fromSnapshot)
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toList());

                            mDataList.postSuccess(allLists);
                        }
                    });
        }

        return mDataList;
    }

    private Notification_UserSubCol fromSnapshot(DocumentSnapshot snapshot) {
        Notification_UserSubCol notification = null;

        Object typeObj = snapshot.get("type");
        if (typeObj instanceof Long) {
            int type = ((Long) typeObj).intValue();

            switch (type) {
                case NotificationType.ADMIN:
                    notification = new AdminNotification();

                    Object thumbnailObj = snapshot.get("thumbnail");
                    if (thumbnailObj instanceof Long) {
                        int thumbnail = ((Long) thumbnailObj).intValue();
                        ((AdminNotification) notification).setThumbnail(thumbnail);
                    }

                    break;

                case NotificationType.TODO:
                    notification = new TodoNotification();

                    String todoId = snapshot.getString("todoId");

                    ((TodoNotification) notification).setTodoId(todoId);

                    break;

                case NotificationType.MATERIAL:
                    notification = new NewMaterialNotification();

                    Object courseIdObj = snapshot.get("courseId");
                    if (courseIdObj instanceof Long) {
                        int courseId = ((Long) courseIdObj).intValue();
                        ((NewMaterialNotification) notification).setCourseId(courseId);
                    }

                    Object materialIdObj = snapshot.get("materialId");
                    if (materialIdObj instanceof Long) {
                        int materialId = ((Long) materialIdObj).intValue();
                        ((NewMaterialNotification) notification).setMaterialId(materialId);
                    }

                    break;

                case NotificationType.ATTENDANCE:
                    notification = new CourseAttendantNotification();
                    String courseCode = snapshot.getString("courseCode");
                    ((CourseAttendantNotification) notification).setCourseCode(courseCode);

                    break;
            }

            if (notification != null) {
                String id = snapshot.getId();
                notification.setId(id);

                notification.setType(type);

                Object notifyTimeObj = snapshot.get("notifyTime");
                if (notifyTimeObj instanceof Long) {
                    long notifyTime = ((Long) notifyTimeObj).intValue();
                    notification.setNotifyTime(notifyTime);
                }

                String title = snapshot.getString("title");
                notification.setTitle(title);

                String description = snapshot.getString("description");
                notification.setDescription(description);
            }

        }

        return notification;
    }
}
