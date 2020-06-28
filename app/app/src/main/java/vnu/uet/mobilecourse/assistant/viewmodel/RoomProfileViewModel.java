package vnu.uet.mobilecourse.assistant.viewmodel;

import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.repository.firebase.ChatRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public class RoomProfileViewModel extends ViewModel {

    private ChatRepository mChatRepo = ChatRepository.getInstance();

    public IStateLiveData<GroupChat> getRoomInfo(String roomId) {
        return mChatRepo.getGroupChatInfo(roomId);
    }

    public IStateLiveData<String> removeMember(String roomId, String memberId) {
        return mChatRepo.removeMember(roomId, memberId);
    }
}
