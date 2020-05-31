package vnu.uet.mobilecourse.assistant.viewmodel;

import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.firebase.UserInfo;
import vnu.uet.mobilecourse.assistant.repository.firebase.StudentRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public class FriendProfileViewModel extends ViewModel {

    private StudentRepository studentRepo = StudentRepository.getInstance();

    public IStateLiveData<UserInfo> getUserInfo(String id) {
        return studentRepo.getStudentById(id);
    }
}
