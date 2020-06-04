package vnu.uet.mobilecourse.assistant.viewmodel;

import java.util.List;

import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.ICourse;
import vnu.uet.mobilecourse.assistant.model.firebase.UserInfo;
import vnu.uet.mobilecourse.assistant.repository.course.CourseRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.StudentRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public class FriendProfileViewModel extends ViewModel {

    private StudentRepository studentRepo = StudentRepository.getInstance();

    private CourseRepository courseRepo = CourseRepository.getInstance();

    public IStateLiveData<UserInfo> getUserInfo(String id) {
        return studentRepo.getStudentById(id);
    }

    public IStateLiveData<List<ICourse>> getCommonCourses(String studentId) {
        return courseRepo.getCommonCourses(studentId);
    }
}
