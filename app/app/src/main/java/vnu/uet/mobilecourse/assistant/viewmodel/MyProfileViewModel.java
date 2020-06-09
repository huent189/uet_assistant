package vnu.uet.mobilecourse.assistant.viewmodel;

import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.UserInfo;
import vnu.uet.mobilecourse.assistant.repository.firebase.StudentRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public class MyProfileViewModel extends ViewModel {

    private StudentRepository studentRepo = StudentRepository.getInstance();

    private static final String STUDENT_ID = User.getInstance().getStudentId();

    public IStateLiveData<UserInfo> getUserInfo() {
        return studentRepo.getStudentById(STUDENT_ID);
    }
}
