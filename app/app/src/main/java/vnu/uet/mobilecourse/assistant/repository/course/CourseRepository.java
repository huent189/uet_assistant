package vnu.uet.mobilecourse.assistant.repository.course;

import android.util.Log;
import androidx.lifecycle.LiveData;
import vnu.uet.mobilecourse.assistant.database.CoursesDatabase;
import vnu.uet.mobilecourse.assistant.database.DAO.CoursesDAO;
import vnu.uet.mobilecourse.assistant.database.DAO.GradeDAO;
import vnu.uet.mobilecourse.assistant.database.DAO.MaterialDAO;
import vnu.uet.mobilecourse.assistant.model.Course;
import vnu.uet.mobilecourse.assistant.model.CourseOverview;
import vnu.uet.mobilecourse.assistant.model.Grade;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.network.HTTPClient;
import vnu.uet.mobilecourse.assistant.network.request.CourseRequest;
import vnu.uet.mobilecourse.assistant.network.response.CoursesResponseCallback;
import vnu.uet.mobilecourse.assistant.network.response.PageContentResponse;
import vnu.uet.mobilecourse.assistant.util.StringUtils;
import vnu.uet.mobilecourse.assistant.work.CourseDataSynchonization;

import java.io.IOException;
import java.util.List;

public class CourseRepository {
    /**
     * Singleton instance
     */
    private static CourseRepository instance;
    private CoursesDAO coursesDAO;
    private MaterialDAO materialDAO;
    private GradeDAO gradeDAO;
    /**
     * Data set
     */
    /**
     * Get singleton instance
     * @return singleton instance
     */
    public static CourseRepository getInstance() {
        if (instance == null) {
            instance = new CourseRepository();
            instance.coursesDAO = CoursesDatabase.getDatabase().coursesDAO();
            instance.materialDAO = CoursesDatabase.getDatabase().materialDAO();
            instance.gradeDAO = CoursesDatabase.getDatabase().gradeDAO();

        }

        return instance;
    }

    public LiveData<List<Course>> getCourses() {
        if(User.getInstance().getLastSynchonizedTime() == -1){
            updateMyCourses();
        }
        else {
            synchonizeAccessTime();
        }
        Runnable task = () -> {
            try {
                new CourseDataSynchonization().checkPageContent(1590300000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        new Thread(task).start();
        return coursesDAO.getMyCourses();
    }

    public void updateMyCourses(){
        CourseRequest request = HTTPClient.getInstance().request(CourseRequest.class);
        request.getMyCoures(User.getInstance().getUserId())
                .enqueue(new CoursesResponseCallback<Course[]>(Course[].class) {
                    @Override
                    public void onSuccess(Course[] response) {
                        CoursesDatabase.databaseWriteExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                for (Course entity:response) {
                                    entity.setTitle(StringUtils.courseTitleFormat(entity.getTitle()));
                                }
                                coursesDAO.insertCourse(response);
                            }
                        });
                    }
                });
    }

    public void updateCourseContent(int courseId){
        CourseRequest request = HTTPClient.getInstance().request(CourseRequest.class);
        request.getCourseContent(courseId + "")
                .enqueue(new CoursesResponseCallback<CourseOverview[]>(CourseOverview[].class) {
                    @Override
                    public void onSuccess(CourseOverview[] response) {
                        Log.d("CourseOverview", "onSuccess: ");
                        CoursesDatabase.databaseWriteExecutor.execute(()->{
                            materialDAO.insertCourseContent(courseId, response);
                            Log.d("getPagesByCourses", "onSuccess: ");
                            int[] ids = {courseId};
                            request.getPagesByCourses(ids).enqueue(
                                    new CoursesResponseCallback<PageContentResponse[]>(PageContentResponse[].class) {
                                        @Override
                                        public void onSuccess(PageContentResponse[] response) {
                                            CoursesDatabase.databaseWriteExecutor.execute(()->{
                                                for(PageContentResponse page : response){
                                                    materialDAO.updatePageContent(page.getContent(),
                                                            page.getTimeModified(), page.getCourseId());
                                                }
                                            });
                                        }
                                    });
                        });

                    }
                });
    }

    public LiveData<List<CourseOverview>> getContent(int courseId){
        updateCourseContent(courseId);
        new CourseActionRepository().triggerCourseView(courseId);
        return materialDAO.getCourseContent(courseId);
    }
    public LiveData<List<Grade>> getGrades(int courseId){
        updateCourseGrade(courseId);
        return gradeDAO.getGrades(courseId);
    }
    public LiveData<Grade> getTotalGrades(int courseId){
        updateCourseGrade(courseId);
        return gradeDAO.getTotalGrade(courseId);
    }
    public void updateCourseGrade(int courseId) {
        HTTPClient.getInstance().request(CourseRequest.class)
                .getCourseGrade(courseId + "", User.getInstance().getUserId())
                .enqueue(new CoursesResponseCallback<Grade[]>(Grade[].class) {
                    @Override
                    public void onSuccess(Grade[] response) {
                        CoursesDatabase.databaseWriteExecutor.execute(() ->{
                            gradeDAO.insertGrade(response);
                        });
                    }
                });
    }
    public void synchonizeAccessTime(){
        HTTPClient.getInstance().request(CourseRequest.class).getMyCoures(User.getInstance().getUserId())
                .enqueue(new CoursesResponseCallback<Course[]>(Course[].class) {
                    @Override
                    public void onSuccess(Course[] response) {
                        for (Course couse :response) {
                            CoursesDatabase.databaseWriteExecutor.execute(() -> {
                                coursesDAO.updateLastAccessTime(couse.getId(), couse.getLastAccessTime());
                            });
                        }
                        User.getInstance().setLastSynchonizedTime(System.currentTimeMillis() / 1000);
                    }
                });
    }
}
