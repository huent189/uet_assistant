package vnu.uet.mobilecourse.assistant.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat_UserSubCol;
import vnu.uet.mobilecourse.assistant.repository.firebase.ChatRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public class ChatViewModel extends ViewModel {

    private ChatRepository chatRepo = ChatRepository.getInstance();

    public IStateLiveData<List<GroupChat_UserSubCol>> getGroupChats() {
        return chatRepo.getAllUserGroupChats();
    }


}
