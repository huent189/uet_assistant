package vnu.uet.mobilecourse.assistant.viewmodel;

import android.widget.Toast;

import java.util.List;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.model.firebase.MemberRole;
import vnu.uet.mobilecourse.assistant.model.firebase.Member_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.Message_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.repository.firebase.ChatRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class ChatRoomViewModel extends ViewModel {

    private ChatRepository mChatRepo = ChatRepository.getInstance();
    private String mCode, mTitle, mType;

    private static final String STUDENT_ID = User.getInstance().getStudentId();
    private static final String STUDENT_NAME = User.getInstance().getName();

    public IStateLiveData<List<Message_GroupChatSubCol>> getAllMessages(String roomId) {
        return mChatRepo.getMessages(roomId);
    }

//    private StateMediatorLiveData<String> createRoom(GroupChat groupChat) {
//        return mChatRepo.createGroupChat(groupChat);
//    }

    public IStateLiveData<String> sendMessage(String roomId, Message_GroupChatSubCol message, List<Member_GroupChatSubCol> members, boolean init) {
        if (init && mType.equals(GroupChat.DIRECT)) {
            return new FirstMessageLiveData(roomId, message, members);
        }

        return mChatRepo.sendMessage(roomId, message, members);
    }

    private StateMediatorLiveData<String> createDirectedChat(String roomId) {
        StateMediatorLiveData<String> liveData = new StateMediatorLiveData<>();
        liveData.postLoading();

        GroupChat groupChat = new GroupChat();

        groupChat.setId(roomId);
        groupChat.setCreatedTime(System.currentTimeMillis() / 1000);
        groupChat.setName(mTitle);
        groupChat.setAvatar(null);

        Member_GroupChatSubCol me = new Member_GroupChatSubCol();
        me.setId(STUDENT_ID);
        me.setName(STUDENT_NAME);
        me.setRole(MemberRole.MEMBER);

        Member_GroupChatSubCol other = new Member_GroupChatSubCol();
        other.setId(mCode);
        other.setName(mTitle);
        other.setRole(MemberRole.MEMBER);

        groupChat.getMembers().add(me);
        groupChat.getMembers().add(other);

//        liveData = createRoom(groupChat);
        liveData = mChatRepo.createGroupChat(groupChat);

        return liveData;
    }

    class FirstMessageLiveData extends StateMediatorLiveData<String> {

        FirstMessageLiveData(String roomId, Message_GroupChatSubCol message, List<Member_GroupChatSubCol> members) {
            postLoading();

            StateMediatorLiveData<String> createLiveData = createDirectedChat(roomId);

            addSource(createLiveData, new Observer<StateModel<String>>() {
                @Override
                public void onChanged(StateModel<String> stateModel) {
                    switch (stateModel.getStatus()) {
                        case LOADING:
                            postLoading();
                            break;

                        case ERROR:
                            postError(stateModel.getError());
                            break;

                        case SUCCESS:
                            postSuccess(CONNECTED_MSG);

                            addSource(mChatRepo.sendMessage(roomId, message, members), new Observer<StateModel<String>>() {
                                @Override
                                public void onChanged(StateModel<String> stateModel) {
                                    switch (stateModel.getStatus()) {
                                        case LOADING:
                                            postLoading();
                                            break;

                                        case ERROR:
                                            postError(stateModel.getError());
                                            break;

                                        case SUCCESS:
                                            postSuccess(stateModel.getData());
                                            break;
                                    }
                                }
                            });

                            removeSource(createLiveData);

                            break;
                    }
                }
            });
        }
    }

    public void setCode(String code) {
        this.mCode = code;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public static final String CONNECTED_MSG = "Hai bạn đã được kết nối";
}
