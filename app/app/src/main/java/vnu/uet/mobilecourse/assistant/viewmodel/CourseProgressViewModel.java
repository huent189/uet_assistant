package vnu.uet.mobilecourse.assistant.viewmodel;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.CourseContent;
import vnu.uet.mobilecourse.assistant.repository.CourseRepository;

public class CourseProgressViewModel extends ViewModel {

    private CourseRepository courseRepo = CourseRepository.getInstance();

    public CourseRepository getCourseRepository() {
        return courseRepo;
    }

    public LiveData<List<CourseContent>> getContent(int courseId) {
        return courseRepo.getContent(courseId);
    }
}
