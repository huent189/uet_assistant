package vnu.uet.mobilecourse.assistant.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;

public class CourseRepository {
    /**
     * Singleton instance
     */
    private static CourseRepository instance;
    private CoursesDAO dao;
    private CourseInfoDAO infoDAO;

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

    public StateLiveData<List<CourseInfo>> getAllCourseInfos() {
        return infoDAO.readAll();
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

    static class MergeCourseLiveData extends StateMediatorLiveData<List<ICourse>> {

        private List<Course> courses = new ArrayList<>();
        private List<CourseInfo> fbCourses = new ArrayList<>();
        private boolean coursesSuccess;
        private boolean fbSuccess;

        MergeCourseLiveData(@NonNull LiveData<List<Course>> coursesLiveData,
                            @NonNull StateLiveData<List<CourseInfo>> fbLiveData) {

            // init with loading state
            postLoading();

            addSource(coursesLiveData, courses -> {
                if (courses == null) {
                    coursesSuccess = false;
                    postLoading();
                } else {
                    coursesSuccess = true;
                    setCourses(courses);

                    if (coursesSuccess && fbSuccess) {
                        List<ICourse> combineData = combineData();
                        postSuccess(combineData);
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
                            List<ICourse> combineData = combineData();
                            postSuccess(combineData);
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

        private List<ICourse> combineData() {
            List<ICourse> merged = new ArrayList<>(courses);

            List<CourseInfo> others = fbCourses.stream().filter(courseInfo -> {
                for (ICourse course : merged) {
                    if (course.getCode().equals(courseInfo.getCode()))
                        return false;
                }

                return true;
            }).collect(Collectors.toList());

            merged.addAll(others);

            return merged;
        }
    }
}
