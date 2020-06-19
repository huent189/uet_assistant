package vnu.uet.mobilecourse.assistant.viewmodel;

import java.util.List;

import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.firebase.Message_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.repository.firebase.ChatRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public class ChatRoomViewModel extends ViewModel {

    private ChatRepository mChatRepo = ChatRepository.getInstance();

    public IStateLiveData<List<Message_GroupChatSubCol>> getAllMessages(String roomId) {
        return mChatRepo.getMessages(roomId);
    }
}
