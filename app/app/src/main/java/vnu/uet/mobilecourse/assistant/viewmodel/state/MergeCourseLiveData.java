package vnu.uet.mobilecourse.assistant.viewmodel.state;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import vnu.uet.mobilecourse.assistant.model.Course;
import vnu.uet.mobilecourse.assistant.model.ICourse;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseInfo;

public class MergeCourseLiveData extends StateMediatorLiveData<List<ICourse>> {

    private List<Course> courses = new ArrayList<>();
    private List<CourseInfo> fbCourses = new ArrayList<>();
    private boolean coursesSuccess;
    private boolean fbSuccess;

    public MergeCourseLiveData(@NonNull LiveData<List<Course>> coursesLiveData,
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