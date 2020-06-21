package vnu.uet.mobilecourse.assistant.work.courses;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.google.firebase.firestore.util.Util;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.database.CoursesDatabase;
import vnu.uet.mobilecourse.assistant.database.querymodel.IdNamePair;
import vnu.uet.mobilecourse.assistant.model.CourseOverview;
import vnu.uet.mobilecourse.assistant.model.Material;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.forum.Discussion;
import vnu.uet.mobilecourse.assistant.model.material.MaterialContent;
import vnu.uet.mobilecourse.assistant.model.material.PageContent;
import vnu.uet.mobilecourse.assistant.model.notification.ForumPostNotification;
import vnu.uet.mobilecourse.assistant.model.notification.NewMaterialNotification;
import vnu.uet.mobilecourse.assistant.model.notification.Notification_UserSubCol;
import vnu.uet.mobilecourse.assistant.network.HTTPClient;
import vnu.uet.mobilecourse.assistant.network.request.CourseRequest;
import vnu.uet.mobilecourse.assistant.repository.course.CourseRepository;
import vnu.uet.mobilecourse.assistant.repository.course.ForumRepository;
import vnu.uet.mobilecourse.assistant.repository.course.MaterialRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.NavigationBadgeRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.NotificationRepository;
import vnu.uet.mobilecourse.assistant.util.NotificationHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CourseSyncDataWorker extends Worker {
    private CourseRequest sender;
    private CoursesDatabase database;
    private MaterialRepository materialRepository;
    private CourseRepository courseRepository;
    private ForumRepository forumRepository;
    private Context mContext;
    private static final String CHANNEL_ID = MaterialContent.class.getSimpleName();
    public CourseSyncDataWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.mContext = context;
        sender = HTTPClient.getInstance().request(CourseRequest.class);
        database = CoursesDatabase.getDatabase();
        materialRepository = MaterialRepository.getInstance();
        courseRepository = CourseRepository.getInstance();
        forumRepository = new ForumRepository();
    }
    @SuppressLint("RestrictedApi")
    private Notification_UserSubCol generateNotification(MaterialContent content, boolean isNew, String courseName){
        NewMaterialNotification notification = new NewMaterialNotification();
        notification.setId(Util.autoId());
        if (isNew){
            notification.setTitle("Bạn có một tài liệu mới");
        } else {
            notification.setTitle("Một tài liệu trong khóa học của bạn đã được cập nhật");
        }
        notification.setDescription(courseName + ": " + content.getName());
        notification.setNotifyTime(System.currentTimeMillis() / 1000);
        notification.setCourseId(content.getCourseId());
        notification.setMaterialId(content.getMaterialId());
        notification.setMaterialType(content.getType());
        return notification;
    }
    @SuppressLint("RestrictedApi")
    private Notification_UserSubCol generateNotification(Discussion discussion){
        ForumPostNotification notification = new ForumPostNotification();
        notification.setId(Util.autoId());
        notification.setTitle("Bạn có một bài đăng mới trên diễn đàn");
        notification.setDescription(discussion.getName());
        notification.setNotifyTime(System.currentTimeMillis() / 1000);
        notification.setDiscussionId(discussion.getId());
        return notification;
    }
    private void pushNotification(Context context, Notification_UserSubCol instance){
        NotificationHelper helper = NotificationHelper.getsInstance();
        Notification notification = helper.build(context, CHANNEL_ID,
                R.drawable.ic_check_circle_24dp, instance.getTitle(), instance.getDescription());
        helper.notify(context, instance.getId(), notification);
    }
    private void pushEmptyNotification(Context context){
        NotificationHelper helper = NotificationHelper.getsInstance();
        Notification notification = helper.build(context, CHANNEL_ID,
                R.drawable.ic_bot_border, "FOR_TEST",
                "Ê m không có thông báo gì mới đâu, t đang test thôi :v");
        helper.notify(context, ((int) (Math.random() * 1000000000)) + "", notification);
    }
    @NonNull
    @Override
    public Result doWork() {
        Log.d("COURSE_SYNC", "doWork:");
        try {
            ArrayList<Integer> courseIds = courseRepository.updateMyCourses();
            ArrayList<Material> updateMaterialList = new ArrayList<>();
            courseIds.forEach(new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) {
                    try {
                        updateMaterialList.addAll(courseRepository.updateCourseContent(integer)
                                .parallelStream().map(CourseOverview::getMaterials)
                                .flatMap(List::stream).collect(Collectors.toList()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            if(updateMaterialList.size() > 0 ){
                Set<String> type = updateMaterialList.stream().map(Material::getType).collect(Collectors.toSet());
                List<MaterialContent> materials = materialRepository.selectiveUpdate(type);
                List<Discussion> discussions = updateForum();
                if(User.getInstance().getEnableSyncNoti()){
                    if(materials.size() != 0){
                        List<IdNamePair> courseNames = database.coursesDAO().getAllCourseName();
                        Map<Integer, String> idNameMap = courseNames.stream()
                                .collect(Collectors.toMap(IdNamePair::getId, IdNamePair::getName));
                        for (MaterialContent content:materials) {
                            Notification_UserSubCol notification;
                            if(content instanceof PageContent && ((PageContent) content).getRevision() > 1){
                                notification = generateNotification(content, false, idNameMap.get(content.getCourseId()));
                            } else {
                                notification = generateNotification(content, true, idNameMap.get(content.getCourseId()));
                            }
                            NotificationRepository.getInstance().add(notification);
                            NavigationBadgeRepository.getInstance().increaseNewNotifications();
                            if(content.getTimeModified() >= User.getInstance().getLastSyncTime()){
                                pushNotification(mContext, notification);
                            }
                        }
                    }
                    if(discussions.size() != 0){
                        discussions.forEach(new Consumer<Discussion>() {
                            @Override
                            public void accept(Discussion discussion) {
                                Notification_UserSubCol notification = generateNotification(discussion);
                                NotificationRepository.getInstance().add(notification);
                                NavigationBadgeRepository.getInstance().increaseNewNotifications();
                                pushNotification(mContext, notification);
                            }
                        });
                    }

                }
                else {
                    User.getInstance().setEnableSyncNoti(true);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();
        }
        return Result.success();
    }
    private List<Discussion> updateForum() throws IOException {
        List<Discussion> updateList = forumRepository.updateAllDiscussion();
        updateList = updateList.stream().filter(discussion -> discussion.getTimeModified() > User.getInstance().getLastSyncTime()).collect(Collectors.toList());
        return updateList;
    }
}
