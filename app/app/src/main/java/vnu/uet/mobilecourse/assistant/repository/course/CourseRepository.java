package vnu.uet.mobilecourse.assistant.repository.course;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import com.google.gson.JsonElement;
import retrofit2.Call;
import vnu.uet.mobilecourse.assistant.database.CoursesDatabase;
import vnu.uet.mobilecourse.assistant.database.DAO.CourseInfoDAO;
import vnu.uet.mobilecourse.assistant.database.DAO.CoursesDAO;
import vnu.uet.mobilecourse.assistant.database.DAO.GradeDAO;
import vnu.uet.mobilecourse.assistant.database.DAO.MaterialDAO;
import vnu.uet.mobilecourse.assistant.model.*;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseInfo;
import vnu.uet.mobilecourse.assistant.network.CourseClient;
import vnu.uet.mobilecourse.assistant.network.request.CourseRequest;
import vnu.uet.mobilecourse.assistant.network.response.CoursesResponseCallback;
import vnu.uet.mobilecourse.assistant.repository.cache.CommonCourseCache;
import vnu.uet.mobilecourse.assistant.util.FbAndCourseMap;
import vnu.uet.mobilecourse.assistant.util.StringUtils;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        new Thread(() -> {
            try {
                updateMyCourses();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
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

    public ArrayList<Integer> updateMyCourses() throws IOException {
        ArrayList<Integer> courseIds = new ArrayList<>();
        Call<JsonElement> call = CourseClient.getInstance().request(CourseRequest.class).getMyCoures(User.getInstance().getUserId());
        CoursesResponseCallback<Course[]> handler = new CoursesResponseCallback<Course[]>(Course[].class) {
            @Override
            public void onSuccess(Course[] response) {
                for (Course entity:response) {
                    entity.setTitle(StringUtils.courseTitleFormat(entity.getTitle()));
                    courseIds.add(entity.getId());
                }
                coursesDAO.insertCourse(response);
            }
        };
        handler.onResponse(call,call.execute());
        return courseIds;
    }

    public List<Material> updateCourseContent(int courseId) throws IOException {
        ArrayList<Material> updateList = new ArrayList<>();
        Call<JsonElement> call = CourseClient.getInstance().request(CourseRequest.class).getCourseContent(courseId);
        CoursesResponseCallback<CourseOverview[]> hanler = new CoursesResponseCallback<CourseOverview[]>(CourseOverview[].class) {
            @Override
            public void onSuccess(CourseOverview[] response) {
                updateList.addAll(materialDAO.insertMaterial(courseId, response));

            }
        };
        hanler.onResponse(call, call.execute());
        return updateList;
    }

    public LiveData<List<CourseOverview>> getContent(int courseId){
        new Thread(() -> {
            try {
                updateCourseContent(courseId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
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
        CourseClient.getInstance().request(CourseRequest.class)
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
//        CourseClient.getInstance().request(CourseRequest.class).getMyCoures(User.getInstance().getUserId())
//                .enqueue(new CoursesResponseCallback<Course[]>(Course[].class) {
//                    @Override
//                    public void onSuccess(Course[] response) {
//                        for (Course course :response) {
//                            CoursesDatabase.databaseWriteExecutor.execute(() -> {
//                                coursesDAO.updateLastAccessTime(course.getId(), course.getLastAccessTime());
//                            });
//                        }
//                        User.getInstance().setEnableSyncNoti(System.currentTimeMillis() / 1000);
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
                    if (FbAndCourseMap.equals(course, otherCourse)) {
//                    if (course.getCode().equals(otherCourse.getCode())) {
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
                    if (FbAndCourseMap.equals(course, courseInfo))
//                    if (course.getCode().equals(courseInfo.getCode()))
                        return false;
                }

                return true;
            }).collect(Collectors.toList());

            merged.addAll(others);

            return merged;
        }
    }
    public LiveData<Double> getProgress(int courseId){
        return coursesDAO.getProgress(courseId);
    }
}
