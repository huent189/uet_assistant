package vnu.uet.mobilecourse.assistant.viewmodel;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.Grade;
import vnu.uet.mobilecourse.assistant.repository.CourseRepository;

public class CourseGradeViewModel extends ViewModel {

    private CourseRepository courseRepo = CourseRepository.getInstance();

    public LiveData<List<Grade>> getCourseGrades(int courseId) {
        return courseRepo.getGrades(courseId);
    }

    public LiveData<Grade> getCourseSummaryGrade(int courseId) {
        return courseRepo.getTotalGrades(courseId);
    }

}
