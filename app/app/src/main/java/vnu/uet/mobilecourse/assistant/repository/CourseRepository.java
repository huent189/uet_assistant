package vnu.uet.mobilecourse.assistant.repository;

import androidx.lifecycle.LiveData;
import vnu.uet.mobilecourse.assistant.database.CoursesDatabase;
import vnu.uet.mobilecourse.assistant.database.DAO.CourseInfoDAO;
import vnu.uet.mobilecourse.assistant.database.DAO.CoursesDAO;
import vnu.uet.mobilecourse.assistant.model.Course;
import vnu.uet.mobilecourse.assistant.model.CourseContent;
import vnu.uet.mobilecourse.assistant.model.Grade;
import vnu.uet.mobilecourse.assistant.model.ICourse;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseInfo;
import vnu.uet.mobilecourse.assistant.network.HTTPClient;
import vnu.uet.mobilecourse.assistant.network.request.CourseRequest;
import vnu.uet.mobilecourse.assistant.network.response.CoursesResponseCallback;
import vnu.uet.mobilecourse.assistant.util.StringUtils;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.MergeCourseLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CourseRepository {
    /**
     * Singleton instance
     */
    private static CourseRepository instance;
    private CoursesDAO dao;
    private CourseInfoDAO infoDAO;

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
            instance.dao = CoursesDatabase.getDatabase().coursesDAO();
            instance.infoDAO = new CourseInfoDAO();
        }

        return instance;
    }



    public LiveData<List<Course>> getCourses() {
        updateMyCourses();

        return dao.getMyCourses();
    }

    public IStateLiveData<List<ICourse>> getFullCourses() {
        updateMyCourses();

        return new MergeCourseLiveData(dao.getMyCourses(), infoDAO.readAll());
    }

    public void updateMyCourses(){
        CourseRequest request = HTTPClient.getInstance().request(CourseRequest.class);
        request.getMyCoures(User.getInstance().getUserId())
                .enqueue(new CoursesResponseCallback<Course[]>(Course[].class) {
                    @Override
                    public void onSucess(Course[] response) {
                        CoursesDatabase.databaseWriteExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                for (Course entity:response) {
                                    entity.setTitle(StringUtils.courseTitleFormat(entity.getTitle()));
                                }
                                dao.insertCourse(response);
                            }
                        });
                    }
                });
    }

    public void updateCourseContent(int courseId){
        HTTPClient.getInstance().request(CourseRequest.class).getCourseContent(courseId + "")
                .enqueue(new CoursesResponseCallback<CourseContent[]>(CourseContent[].class) {
                    @Override
                    public void onSucess(CourseContent[] response) {
                        CoursesDatabase.databaseWriteExecutor.execute(()->{
                            dao.insertCourseContent(courseId, response);
                        });

                    }
                });
    }

    public LiveData<List<CourseContent>> getContent(int courseId){
        updateCourseContent(courseId);
        return dao.getCourseContent(courseId);
    }
    public LiveData<List<Grade>> getGrades(int courseId){
        updateCourseGrade(courseId);
        return dao.getGrades(courseId);
    }
    public LiveData<Grade> getTotalGrades(int courseId){
        updateCourseGrade(courseId);
        return dao.getTotalGrade(courseId);
    }
    public void updateCourseGrade(int courseId) {
        HTTPClient.getInstance().request(CourseRequest.class)
                .getCourseGrade(courseId + "", User.getInstance().getUserId())
                .enqueue(new CoursesResponseCallback<Grade[]>(Grade[].class) {
                    @Override
                    public void onSucess(Grade[] response) {
                        CoursesDatabase.databaseWriteExecutor.execute(() ->{
                            dao.insertGrade(response);
                        });
                    }
                });
    }
}
