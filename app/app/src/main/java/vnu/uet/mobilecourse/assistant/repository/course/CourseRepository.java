package vnu.uet.mobilecourse.assistant.repository.course;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import vnu.uet.mobilecourse.assistant.database.CoursesDatabase;
import vnu.uet.mobilecourse.assistant.database.DAO.CourseInfoDAO;
import vnu.uet.mobilecourse.assistant.database.DAO.CoursesDAO;
import vnu.uet.mobilecourse.assistant.database.DAO.GradeDAO;
import vnu.uet.mobilecourse.assistant.database.DAO.MaterialDAO;
import vnu.uet.mobilecourse.assistant.model.*;
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

    /**
     * Get course info (such as credit number, participant number, sessions, ...)
     * @param courseId of selected course
     * @return live data contains result of this operation
     */
    public StateMediatorLiveData<CourseInfo> getCourseInfo(String courseId) {
        return infoDAO.read(courseId);
    }

    /**
     * @return all participate course information
     */
    public StateLiveData<List<CourseInfo>> getAllCourseInfos() {
        return infoDAO.readAll();
    }

    public LiveData<List<Course>> getCourses() {
        updateMyCourses();
        return coursesDAO.getMyCourses();
    }

    /**
     * @return courses from courses.uet.vnu.edu.vn and courses from firebase
     */
    public StateMediatorLiveData<List<ICourse>> getFullCourses() {
        return new MergeCourseLiveData(getCourses(), infoDAO.readAll());
    }

    /**
     * Get common course between current user and a specific student
     * @param otherId - other student's code
     */
    public IStateLiveData<List<ICourse>> getCommonCourses(String otherId) {
        if (commonCourseCache.containsKey(otherId)) {
            return commonCourseCache.get(otherId);

        } else {
            StateMediatorLiveData<List<ICourse>> myCourses = getFullCourses();
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
        HTTPClient.getInstance().request(CourseRequest.class).getCourseContent(courseId + "")
                .enqueue(new CoursesResponseCallback<CourseOverview[]>(CourseOverview[].class) {
                    @Override
                    public void onSuccess(CourseOverview[] response) {
                        CoursesDatabase.databaseWriteExecutor.execute(()->{
                            materialDAO.insertCourseContent(courseId, response);
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

//    public void synchronizeAccessTime(){
//        HTTPClient.getInstance().request(CourseRequest.class).getMyCoures(User.getInstance().getUserId())
//                .enqueue(new CoursesResponseCallback<Course[]>(Course[].class) {
//                    @Override
//                    public void onSuccess(Course[] response) {
//                        for (Course course :response) {
//                            CoursesDatabase.databaseWriteExecutor.execute(() -> {
//                                coursesDAO.updateLastAccessTime(course.getId(), course.getLastAccessTime());
//                            });
//                        }
//                        User.getInstance().setLastSynchonizedTime(System.currentTimeMillis() / 1000);
//                    }
//                });
//    }

    static class CommonCourseLiveData extends StateMediatorLiveData<List<ICourse>> {

        private List<ICourse> mMyCourses;
        private List<CourseInfo> mOtherCourses;
        private boolean mMySuccess;
        private boolean mOtherSuccess;

        CommonCourseLiveData(@NonNull StateMediatorLiveData<List<ICourse>> my,
                             @NonNull StateLiveData<List<CourseInfo>> other) {

            // init with loading state
            postLoading();

            addSource(my, stateModel -> {
                switch (stateModel.getStatus()) {
                    case ERROR:
                        mMySuccess = false;
                        postError(stateModel.getError());
                        break;

                    case LOADING:
                        mMySuccess = false;
                        postLoading();
                        break;

                    case SUCCESS:
                        mMySuccess = true;
                        setMyCourses(stateModel.getData());

                        if (mMySuccess && mOtherSuccess) {
                            List<ICourse> combineData = combineData();
                            postSuccess(combineData);
                        }
                }
            });

            addSource(other, stateModel -> {
                switch (stateModel.getStatus()) {
                    case ERROR:
                        mOtherSuccess = false;
                        postError(stateModel.getError());
                        break;

                    case LOADING:
                        mOtherSuccess = false;
                        postLoading();
                        break;

                    case SUCCESS:
                        mOtherSuccess = true;
                        setOtherCourses(stateModel.getData());

                        if (mMySuccess && mOtherSuccess) {
                            List<ICourse> combineData = combineData();
                            postSuccess(combineData);
                        }
                }
            });
        }

        private List<ICourse> combineData() {
            List<ICourse> commonCourses = new ArrayList<>();

            for (ICourse course : mMyCourses) {
                for (CourseInfo otherCourse : mOtherCourses) {
                    if (course.getCode().equals(otherCourse.getCode())) {
                        commonCourses.add(course);
                        break;
                    }
                }
            }

            return commonCourses;
        }

        private void setMyCourses(List<ICourse> myCourses) {
            this.mMyCourses = myCourses.stream().filter(Objects::nonNull).collect(Collectors.toList());
        }

        private void setOtherCourses(List<CourseInfo> otherCourses) {
            this.mOtherCourses = otherCourses.stream().filter(Objects::nonNull).collect(Collectors.toList());
        }
    }

    static class MergeCourseLiveData extends StateMediatorLiveData<List<ICourse>> {

        private List<Course> mCourses = new ArrayList<>();
        private List<CourseInfo> mFbCourses = new ArrayList<>();
        private boolean mCoursesSuccess;
        private boolean mFirebaseSuccess;

        MergeCourseLiveData(@NonNull LiveData<List<Course>> coursesLiveData,
                            @NonNull StateLiveData<List<CourseInfo>> fbLiveData) {

            // init with loading state
            postLoading();

            addSource(coursesLiveData, courses -> {
                if (courses == null) {
                    mCoursesSuccess = false;
                    postLoading();
                } else {
                    mCoursesSuccess = true;
                    setCourses(courses);

                    if (mCoursesSuccess && mFirebaseSuccess) {
                        List<ICourse> combineData = combineData();
                        postSuccess(combineData);
                    }
                }
            });

            addSource(fbLiveData, stateModel -> {
                switch (stateModel.getStatus()) {
                    case ERROR:
                        mFirebaseSuccess = false;
                        postError(stateModel.getError());
                        break;

                    case LOADING:
                        mFirebaseSuccess = false;
                        postLoading();
                        break;

                    case SUCCESS:
                        mFirebaseSuccess = true;
                        setFbCourses(stateModel.getData());

                        if (mCoursesSuccess && mFirebaseSuccess) {
                            List<ICourse> combineData = combineData();
                            postSuccess(combineData);
                        }
                }
            });
        }

        private void setCourses(List<Course> courses) {
            this.mCourses = courses;
        }

        private void setFbCourses(List<CourseInfo> fbCourses) {
            this.mFbCourses = fbCourses;
        }

        private List<ICourse> combineData() {
            List<ICourse> merged = new ArrayList<>(mCourses);

            List<CourseInfo> others = mFbCourses.stream().filter(courseInfo -> {
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
