package vnu.uet.mobilecourse.assistant.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.Course;
import vnu.uet.mobilecourse.assistant.repository.CourseRepository;

import java.util.List;

public class CoursesViewModel extends ViewModel {

    private LiveData<List<Course>> courses;

    private CourseRepository repository;

    public CoursesViewModel() {
    }

    public void initialize() {
        if (courses == null) {
            repository = CourseRepository.getInstance();
            courses = repository.getCourses();
        }
    }

    public LiveData<List<Course>> getCourses() {
        return courses;
    }
}