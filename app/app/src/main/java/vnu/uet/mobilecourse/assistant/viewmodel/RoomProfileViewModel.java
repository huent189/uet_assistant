package vnu.uet.mobilecourse.assistant.viewmodel;

import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.model.firebase.Member_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.repository.firebase.ChatRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public class RoomProfileViewModel extends ViewModel {

    private ChatRepository mChatRepo = ChatRepository.getInstance();

    public IStateLiveData<GroupChat> getRoomInfo(String roomId) {
        return mChatRepo.getGroupChatInfo(roomId);
    }

    public IStateLiveData<String> removeMember(GroupChat room, String memberId) {
        String[] memberIds = room.getMembers().stream()
                .map(Member_GroupChatSubCol::getId)
                .toArray(String[]::new);

        return mChatRepo.removeMember(room.getId(), memberIds, memberId);
    }
}
