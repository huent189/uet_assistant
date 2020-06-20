package vnu.uet.mobilecourse.assistant.viewmodel;

import java.util.List;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.model.firebase.MemberRole;
import vnu.uet.mobilecourse.assistant.model.firebase.Member_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.Message_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.UserInfo;
import vnu.uet.mobilecourse.assistant.repository.firebase.ChatRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.FirebaseUserRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.StudentRepository;
import vnu.uet.mobilecourse.assistant.util.FirebaseStructureId;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class ChatRoomViewModel extends ViewModel {

    private ChatRepository mChatRepo = ChatRepository.getInstance();
    private LifecycleOwner mLifecycleOwner;
    private String mCode, mTitle, mType;

    private static final String STUDENT_ID = User.getInstance().getStudentId();
    private static String STUDENT_NAME;

    public IStateLiveData<List<Message_GroupChatSubCol>> getAllMessages(String roomId) {
        return mChatRepo.getMessages(roomId);
    }

    private StateLiveData<GroupChat> createRoom(GroupChat groupChat) {
        return mChatRepo.createGroupChat(groupChat);
    }

    public IStateLiveData<String> sendMessage(String roomId, Message_GroupChatSubCol message, boolean init) {
        if (init && mType.equals(GroupChat.DIRECT)) {
            createDirectedChat(roomId);
        }

        return mChatRepo.sendMessage(roomId, message);
    }

    public IStateLiveData<GroupChat> createDirectedChat(String roomId) {
        final IStateLiveData[] liveData = new StateLiveData[]{new StateLiveData<GroupChat>()};
        liveData[0].postLoading();

        GroupChat groupChat = new GroupChat();

        groupChat.setId(roomId);
        groupChat.setCreatedTime(System.currentTimeMillis() / 1000);
        groupChat.setName(mTitle);
        groupChat.setAvatar(null);

        Member_GroupChatSubCol me = new Member_GroupChatSubCol();
        me.setId(STUDENT_ID);
        me.setRole(MemberRole.MEMBER);

        Member_GroupChatSubCol other = new Member_GroupChatSubCol();
        other.setId(mCode);
        other.setName(mTitle);
        other.setRole(MemberRole.MEMBER);

        groupChat.getMembers().add(me);
        groupChat.getMembers().add(other);

        if (STUDENT_NAME == null) {
            StudentRepository.getInstance()
                    .getStudentById(STUDENT_ID).observe(mLifecycleOwner, stateModel -> {
                        switch (stateModel.getStatus()) {
                            case LOADING:
                                liveData[0].postLoading();
                                break;

                            case ERROR:
                                liveData[0].postError(stateModel.getError());
                                break;

                            case SUCCESS:
                                STUDENT_NAME = stateModel.getData().getName();
                                me.setName(STUDENT_NAME);
                                liveData[0] = createRoom(groupChat);
                        }
                    });
        } else {
            me.setName(STUDENT_NAME);
            liveData[0] = createRoom(groupChat);
        }

        return liveData[0];
    }

    public void setLifecycleOwner(LifecycleOwner mLifecycleOwner) {
        this.mLifecycleOwner = mLifecycleOwner;
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
}
