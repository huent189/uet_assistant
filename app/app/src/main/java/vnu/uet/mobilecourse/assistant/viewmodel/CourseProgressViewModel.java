package vnu.uet.mobilecourse.assistant.viewmodel;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.CourseOverview;
import vnu.uet.mobilecourse.assistant.repository.course.CourseRepository;

public class CourseProgressViewModel extends ViewModel {

    private CourseRepository mCourseRepo = CourseRepository.getInstance();

    public CourseRepository getCourseRepository() {
        return mCourseRepo;
    }

    public LiveData<List<CourseOverview>> getContent(int courseId) {
        return mCourseRepo.getContent(courseId);
    }
}
