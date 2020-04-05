package vnu.uet.mobilecourse.assistant.ui.viewmodel;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.ui.model.Course;
import vnu.uet.mobilecourse.assistant.ui.repository.CourseRepository;

public class CoursesViewModel extends ViewModel {

    private MutableLiveData<List<Course>> courses;

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