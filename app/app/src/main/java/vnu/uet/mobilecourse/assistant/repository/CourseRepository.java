package vnu.uet.mobilecourse.assistant.repository;

import androidx.lifecycle.LiveData;
import vnu.uet.mobilecourse.assistant.database.CoursesDatabase;
import vnu.uet.mobilecourse.assistant.database.DAO.CoursesDAO;
import vnu.uet.mobilecourse.assistant.model.Course;
import vnu.uet.mobilecourse.assistant.model.CourseContent;
import vnu.uet.mobilecourse.assistant.model.Grade;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseInfo;
import vnu.uet.mobilecourse.assistant.network.HTTPClient;
import vnu.uet.mobilecourse.assistant.network.request.CourseRequest;
import vnu.uet.mobilecourse.assistant.network.response.CoursesResponseCallback;
import vnu.uet.mobilecourse.assistant.util.StringUtils;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

import java.util.ArrayList;
import java.util.List;

public class CourseRepository {
    /**
     * Singleton instance
     */
    private static CourseRepository instance;
    private CoursesDAO dao;
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
        }

        return instance;
    }

    public class MergeCourseLiveData extends StateMediatorLiveData<List<Course>> {
        private List<Course> courses = new ArrayList<>();
        private List<CourseInfo> fbCourses = new ArrayList<>();
        private boolean coursesSuccess;
        private boolean fbSuccess;

        public MergeCourseLiveData(LiveData<List<Course>> coursesLiveData,
                                   StateLiveData<List<CourseInfo>> fbLiveData) {
            postLoading();

            addSource(coursesLiveData, courses -> {
                if (courses == null) {
                    coursesSuccess = false;
                    postLoading();
                } else {
                    coursesSuccess = true;
                    setCourses(courses);

                    if (coursesSuccess && fbSuccess) {

                    }
                }
            });

            addSource(fbLiveData, stateModel -> {
                switch (stateModel.getStatus()) {
                    case ERROR:
                        fbSuccess = false;
                        postError(stateModel.getError());
                        break;

                    case LOADING:
                        fbSuccess = false;
                        postLoading();
                        break;

                    case SUCCESS:
                        fbSuccess = true;
                        setFbCourses(stateModel.getData());

                        if (coursesSuccess && fbSuccess) {

                        }
                }
            });
        }

        private void setCourses(List<Course> courses) {
            this.courses = courses;
        }

        private void setFbCourses(List<CourseInfo> fbCourses) {
            this.fbCourses = fbCourses;
        }

        private List<Course> combineData() {

        }

    }

    public LiveData<List<Course>> getCourses() {
        updateMyCourses();

        StateMediatorLiveData<List<Course>> merger = new StateMediatorLiveData<>(new StateModel<>(StateStatus.LOADING));
        merger.

        return dao.getMyCourses();
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
