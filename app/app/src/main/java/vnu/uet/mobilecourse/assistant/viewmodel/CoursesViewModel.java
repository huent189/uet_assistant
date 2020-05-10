package vnu.uet.mobilecourse.assistant.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.Course;
import vnu.uet.mobilecourse.assistant.repository.CourseRepository;
import vnu.uet.mobilecourse.assistant.view.course.CoursesFragment;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CoursesViewModel extends ViewModel {

    private LiveData<List<Course>> courses;

    private CourseRepository repository;

    private CoursesFragment view;

    private static final int MAX_RECENTLY_INDEX = 4;

    public void initialize() {
        if (courses == null) {
            repository = CourseRepository.getInstance();
            courses = repository.getCourses();
        }
    }

    public LiveData<List<Course>> getCourses() {
        return courses;
    }

    public LiveData<List<Course>> getRecentlyCourses() {
        MutableLiveData<List<Course>> liveData = new MutableLiveData<>();

        if (view != null) {
            courses.observe(view.getViewLifecycleOwner(), courses -> {
                if (courses == null) {
                    liveData.postValue(null);
                    return;
                }

                List<Course> recentlyCourses = courses
                        .stream()
                        .sorted((course1, course2) -> {
                            Date lassAccessTime1 = course1.getLastAccessTime();
                            Date lassAccessTime2 = course2.getLastAccessTime();

                            int comparision = 1;//lassAccessTime1.compareTo(lassAccessTime2);

                            // reverse from newest to oldest
                            comparision *= -1;

                            return comparision;
                        })
                        .collect(Collectors.toList())
                        .subList(0, MAX_RECENTLY_INDEX);

                liveData.postValue(recentlyCourses);
            });
        }

        return liveData;
    }

    public void setView(CoursesFragment view) {
        this.view = view;
    }

    public CoursesFragment getView() {
        return view;
    }
}