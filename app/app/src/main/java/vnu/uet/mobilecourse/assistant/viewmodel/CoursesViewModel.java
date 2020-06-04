package vnu.uet.mobilecourse.assistant.viewmodel;

import java.util.List;
import java.util.stream.Collectors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.Course;
import vnu.uet.mobilecourse.assistant.model.ICourse;
import vnu.uet.mobilecourse.assistant.repository.course.CourseRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public class CoursesViewModel extends ViewModel {

    private CourseRepository mCourseRepo = CourseRepository.getInstance();

    private static final int MAX_RECENTLY_INDEX = 4;

    public IStateLiveData<List<ICourse>> getCourses() {
        return mCourseRepo.getFullCourses();
    }

    public LiveData<List<Course>> getRecentlyCourses() {
        MediatorLiveData<List<Course>> liveData = new MediatorLiveData<>();

        liveData.addSource(mCourseRepo.getCourses(), courses -> {
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
}