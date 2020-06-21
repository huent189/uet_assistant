package vnu.uet.mobilecourse.assistant.viewmodel;

import java.util.List;

import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.UserInfo;
import vnu.uet.mobilecourse.assistant.repository.firebase.ChatRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.StudentRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public class SearchStudentViewModel extends ViewModel {

    private StudentRepository studentRepo = StudentRepository.getInstance();

    public IStateLiveData<UserInfo> searchStudent(String id) {
        return studentRepo.getStudentById(id);
    }

    private ChatRepository chatRepo = ChatRepository.getInstance();

    public IStateLiveData<List<GroupChat_UserSubCol>> getGroupChats() {
        return chatRepo.getAllUserGroupChats();
    }
}
