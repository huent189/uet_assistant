package vnu.uet.mobilecourse.assistant.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.stream.Collectors;

import vnu.uet.mobilecourse.assistant.model.Course;
import vnu.uet.mobilecourse.assistant.repository.course.CourseRepository;

public class CoursesViewModel extends ViewModel {

    private LiveData<List<Course>> mCourses;

    private CourseRepository mCourseRepo;

//    private CoursesFragment view;

    private static final int MAX_RECENTLY_INDEX = 4;

    public void initialize() {
        if (mCourses == null) {
            mCourseRepo = CourseRepository.getInstance();
            mCourses = mCourseRepo.getCourses();
        }
    }

    public LiveData<List<Course>> getCourses() {
        return mCourses;
    }

    public LiveData<List<Course>> getRecentlyCourses() {
        MediatorLiveData<List<Course>> liveData = new MediatorLiveData<>();

        liveData.addSource(mCourses, courses -> {
            if (courses == null) {
                liveData.postValue(null);
                return;
            }

            List<Course> recentlyCourses = courses
                    .stream()
                    .sorted((course1, course2) -> {
                        long lastAccessTime1 = course1.getLastAccessTime();
                        long lastAccessTime2 = course2.getLastAccessTime();

                        int comparision = Long.compare(lastAccessTime1, lastAccessTime2);

                        // reverse from newest to oldest
                        comparision *= -1;

                        return comparision;
                    })
                    .collect(Collectors.toList());


            if (recentlyCourses.size() > MAX_RECENTLY_INDEX)
                recentlyCourses = recentlyCourses.subList(0, MAX_RECENTLY_INDEX);

            liveData.postValue(recentlyCourses);
        });

        return liveData;
    }

//    public void setView(CoursesFragment view) {
//        this.view = view;
//    }

//    public CoursesFragment getView() {
//        return view;
//    }
}