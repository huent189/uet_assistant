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
import vnu.uet.mobilecourse.assistant.alarm.scheduler.SubmissionScheduler;
import vnu.uet.mobilecourse.assistant.database.CoursesDatabase;
import vnu.uet.mobilecourse.assistant.database.querymodel.IdNamePair;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.event.CourseSubmissionEvent;
import vnu.uet.mobilecourse.assistant.model.forum.Discussion;
import vnu.uet.mobilecourse.assistant.model.forum.InterestedDiscussion;
import vnu.uet.mobilecourse.assistant.model.forum.Post;
import vnu.uet.mobilecourse.assistant.model.material.AssignmentContent;
import vnu.uet.mobilecourse.assistant.model.material.MaterialContent;
import vnu.uet.mobilecourse.assistant.model.material.QuizNoGrade;
import vnu.uet.mobilecourse.assistant.model.notification.ForumPostNotification;
import vnu.uet.mobilecourse.assistant.model.notification.NewMaterialNotification;
import vnu.uet.mobilecourse.assistant.model.notification.Notification_UserSubCol;
import vnu.uet.mobilecourse.assistant.repository.course.CourseRepository;
import vnu.uet.mobilecourse.assistant.repository.course.ForumRepository;
import vnu.uet.mobilecourse.assistant.repository.course.MaterialRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.NavigationBadgeRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.NotificationRepository;
import vnu.uet.mobilecourse.assistant.util.NotificationHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CourseSyncDataWorker extends Worker {
    private CoursesDatabase database;
    private MaterialRepository materialRepository;
    private CourseRepository courseRepository;
    private ForumRepository forumRepository;
    private Context mContext;
    private static final String CHANNEL_ID = MaterialContent.class.getSimpleName();
    public CourseSyncDataWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.mContext = context;
        database = CoursesDatabase.getDatabase();
        materialRepository = MaterialRepository.getInstance();
        courseRepository = CourseRepository.getInstance();
        forumRepository = new ForumRepository();
    }
    @SuppressLint("RestrictedApi")
    private Notification_UserSubCol generateNotification(MaterialContent content, String courseName){
        NewMaterialNotification notification = new NewMaterialNotification();
        notification.setId(Util.autoId());
        notification.setTitle("Cập nhật tài liệu");
        notification.setDescription(courseName + ": " + content.getName());
        notification.setNotifyTime(System.currentTimeMillis() / 1000);
        notification.setCourseId(content.getCourseId());
        notification.setMaterialId(content.getMaterialId());
        notification.setMaterialType(content.getType());
        return notification;
    }
    @SuppressLint("RestrictedApi")
    private Notification_UserSubCol generateNotification(Discussion discussion) throws IOException {
        ForumPostNotification notification = new ForumPostNotification();
        List<Post> posts = forumRepository.updatePostByDiscussion(discussion.getId());
        notification.setId(Util.autoId());
        notification.setNotifyTime(System.currentTimeMillis() / 1000);
        notification.setDiscussionId(discussion.getId());
        if(discussion.getTimeModified() == discussion.getTimeCreated()){
            notification.setTitle(discussion.getAuthorName() + " vừa tạo cuộc thảo luận mới ");
            notification.setDescription(discussion.getName() + "\n" + discussion.getMessage());
            return notification;
        }
        List<String> postAuthor = posts.stream().sorted(new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                if(o1.getTimeCreated() == o2.getTimeCreated()) return 0;
                return o1.getTimeCreated() < o2.getTimeCreated() ? -1 : 1;
            }
        }).map(post -> post.getAuthorName()).distinct().collect(Collectors.toList());

        if(postAuthor.size() == 1){
            notification.setDescription(postAuthor.get(0) + " vừa trả lời cuộc thảo luận này");
        } else {
            notification.setDescription(postAuthor.get(0) + " và "+ (postAuthor.size() - 1) + " người khác vừa trả lời cuộc thảo luận này");
        }
        notification.setTitle(discussion.getName());
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
            User.getInstance().setLastSyncTime(System.currentTimeMillis() / 1000);
            ArrayList<Integer> courseIds = courseRepository.updateMyCourses();
            courseIds.forEach(new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) {
                    try {
                        courseRepository.updateCourseContent(integer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            List<MaterialContent> materials = materialRepository.updateAll();
            List<Discussion> discussions = getFollowingDiscussionUpdate();
            scheduleSubmissionEvent(materials.stream().filter(m -> m instanceof QuizNoGrade).map(MaterialContent::getId).collect(Collectors.toList()),
                    materials.stream().filter(m -> m instanceof AssignmentContent).map(MaterialContent::getId).collect(Collectors.toList()));
            if(materials.size() != 0){
                List<IdNamePair> courseNames = database.coursesDAO().getAllCourseName();
                Map<Integer, String> idNameMap = courseNames.stream()
                        .collect(Collectors.toMap(IdNamePair::getId, IdNamePair::getName));
                for (MaterialContent content:materials) {
                    Notification_UserSubCol notification;
                    notification = generateNotification(content, idNameMap.get(content.getCourseId()));
                    NotificationRepository.getInstance().add(notification);
                    NavigationBadgeRepository.getInstance().increaseNewNotifications();
                    pushNotification(mContext, notification);
                }
            }
            if(User.getInstance().getEnableSyncNoti()){
                if(discussions.size() != 0){
                    discussions.forEach(new Consumer<Discussion>() {
                        @Override
                        public void accept(Discussion discussion) {
                            try {
                                Notification_UserSubCol notification = generateNotification(discussion);
                                NotificationRepository.getInstance().add(notification);
                                NavigationBadgeRepository.getInstance().increaseNewNotifications();
                                pushNotification(mContext, notification);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
            else {
                User.getInstance().setEnableSyncNoti(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return Result.success();
    }
    private List<Discussion> getFollowingDiscussionUpdate() throws IOException, ExecutionException, InterruptedException {
        List<Discussion> updateList = forumRepository.updateAllDiscussion();
        List<InterestedDiscussion> interested = forumRepository.getInterestedSynchronize();
        return updateList.stream().filter(discussion -> {
            if (discussion.getTimeCreated() == discussion.getTimeModified()) return true;
            for(InterestedDiscussion i : interested){
                if(discussion.getId() == i.getDiscussionId()){
                    return  true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }
    private void scheduleSubmissionEvent(List<Integer> quizIds, List<Integer> assignIds){
        List<CourseSubmissionEvent> events = materialRepository.getSubmissionEventFromNow(quizIds, assignIds);
        events.forEach(courseSubmissionEvent -> SubmissionScheduler.getInstance(mContext).schedule(courseSubmissionEvent));
    }
}
