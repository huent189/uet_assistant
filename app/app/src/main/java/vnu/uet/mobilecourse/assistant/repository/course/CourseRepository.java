package vnu.uet.mobilecourse.assistant.repository.course;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.lifecycle.Observer;
import vnu.uet.mobilecourse.assistant.database.CoursesDatabase;
import vnu.uet.mobilecourse.assistant.database.DAO.CourseInfoDAO;
import vnu.uet.mobilecourse.assistant.database.DAO.CoursesDAO;
import vnu.uet.mobilecourse.assistant.database.DAO.GradeDAO;
import vnu.uet.mobilecourse.assistant.database.DAO.MaterialDAO;
import vnu.uet.mobilecourse.assistant.model.Course;
import vnu.uet.mobilecourse.assistant.model.CourseContent;
import vnu.uet.mobilecourse.assistant.model.Grade;
import vnu.uet.mobilecourse.assistant.model.ICourse;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseInfo;
import vnu.uet.mobilecourse.assistant.network.HTTPClient;
import vnu.uet.mobilecourse.assistant.network.request.CourseRequest;
import vnu.uet.mobilecourse.assistant.network.response.CoursesResponseCallback;
import vnu.uet.mobilecourse.assistant.repository.cache.CommonCourseCache;
import vnu.uet.mobilecourse.assistant.util.StringUtils;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class CourseRepository {
    /**
     * Singleton instance
     */
    private static CourseRepository instance;

    /**
     * Data set
     */
    private CoursesDAO coursesDAO;
    private MaterialDAO materialDAO;
    private GradeDAO gradeDAO;
    private CourseInfoDAO infoDAO;
    private CommonCourseCache commonCourseCache;

    /**
     * Get singleton instance
     * @return singleton instance
     */
    public static CourseRepository getInstance() {
        if (instance == null) {
            instance = new CourseRepository();
        }

        return instance;
    }

    private CourseRepository() {
        coursesDAO = CoursesDatabase.getDatabase().coursesDAO();
        materialDAO = CoursesDatabase.getDatabase().materialDAO();
        gradeDAO = CoursesDatabase.getDatabase().gradeDAO();

        infoDAO = new CourseInfoDAO();

        commonCourseCache = new CommonCourseCache();
    }

    public StateMediatorLiveData<CourseInfo> getCourseInfo(String courseId) {
        return infoDAO.read(courseId);
    }

    public StateLiveData<List<CourseInfo>> getAllCourseInfos() {
        return infoDAO.readAll();
    }

    public LiveData<List<Course>> getCourses() {
        if(User.getInstance().getLastSynchonizedTime() == -1){
            updateMyCourses();
        }
        else {
            synchonizeAccessTime();
        }
        return coursesDAO.getMyCourses();
    }

    public IStateLiveData<List<ICourse>> getFullCourses() {
        updateMyCourses();

        return new MergeCourseLiveData(getCourses(), infoDAO.readAll());
    }

    public IStateLiveData<List<ICourse>> getCommonCourses(String otherId) {
        if (commonCourseCache.containsKey(otherId)) {
            return commonCourseCache.get(otherId);

        } else {
            StateLiveData<List<CourseInfo>> myCourses = infoDAO.readAll();
            StateLiveData<List<CourseInfo>> otherCourses = infoDAO.getParticipateCourses(otherId);

            IStateLiveData<List<ICourse>> commonCourses = new CommonCourseLiveData(myCourses, otherCourses);

            commonCourseCache.put(otherId, commonCourses);

            return commonCourses;
        }
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
                                coursesDAO.insertCourse(response);
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
                            materialDAO.insertCourseContent(courseId, response);
                        });

                    }
                });
    }

    public LiveData<List<CourseContent>> getContent(int courseId){
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
                    public void onSucess(Grade[] response) {
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
                    public void onSucess(Course[] response) {
                        for (Course couse :response) {
                            CoursesDatabase.databaseWriteExecutor.execute(() -> {
                                coursesDAO.updateLastAccessTime(couse.getId(), couse.getLastAccessTime());
                            });
                        }
                        User.getInstance().setLastSynchonizedTime(System.currentTimeMillis() / 1000);
                    }
                });
    }

    static class CommonCourseLiveData extends StateMediatorLiveData<List<ICourse>> {

        private List<CourseInfo> myCourses;
        private List<CourseInfo> otherCourses;
        private boolean mySuccess;
        private boolean otherSuccess;

        CommonCourseLiveData(@NonNull StateLiveData<List<CourseInfo>> my,
                             @NonNull StateLiveData<List<CourseInfo>> other) {

            // init with loading state
            postLoading();

            addSource(my, stateModel -> {
                switch (stateModel.getStatus()) {
                    case ERROR:
                        mySuccess = false;
                        postError(stateModel.getError());
                        break;

                    case LOADING:
                        mySuccess = false;
                        postLoading();
                        break;

                    case SUCCESS:
                        mySuccess = true;
                        setMyCourses(stateModel.getData());

                        if (mySuccess && otherSuccess) {
                            List<ICourse> combineData = combineData();
                            postSuccess(combineData);
                        }
                }
            });

            addSource(other, stateModel -> {
                switch (stateModel.getStatus()) {
                    case ERROR:
                        otherSuccess = false;
                        postError(stateModel.getError());
                        break;

                    case LOADING:
                        otherSuccess = false;
                        postLoading();
                        break;

                    case SUCCESS:
                        otherSuccess = true;
                        setOtherCourses(stateModel.getData());

                        if (mySuccess && otherSuccess) {
                            List<ICourse> combineData = combineData();
                            postSuccess(combineData);
                        }
                }
            });
        }

        private List<ICourse> combineData() {
            List<ICourse> commonCourses = new ArrayList<>();

            for (CourseInfo course : myCourses) {
                if (otherCourses.contains(course)) {
                    commonCourses.add(course);
                }
            }

            return commonCourses;
        }

        public void setMyCourses(List<CourseInfo> myCourses) {
            this.myCourses = myCourses;
        }

        public void setOtherCourses(List<CourseInfo> otherCourses) {
            this.otherCourses = otherCourses;
        }
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
