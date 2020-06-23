package vnu.uet.mobilecourse.assistant.viewmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.IStudent;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.Connection;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.MemberRole;
import vnu.uet.mobilecourse.assistant.model.firebase.Member_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.UserInfo;
import vnu.uet.mobilecourse.assistant.repository.firebase.ChatRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.StudentRepository;
import vnu.uet.mobilecourse.assistant.util.FirebaseStructureId;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class AddMemberViewModel extends ViewModel {

    private StudentRepository mStudentRepo = StudentRepository.getInstance();
    private ChatRepository mChatRepo = ChatRepository.getInstance();

    private static final String USER_ID = User.getInstance().getStudentId();

    private List<IStudent> mSelectedList = new ArrayList<>();

    public IStateLiveData<UserInfo> searchStudent(String id) {
        return mStudentRepo.getStudentById(id);
    }

    public void addMember(IStudent student) {
        mSelectedList.add(student);
    }

    public void removeMember(IStudent student) {
        mSelectedList.removeIf(stu -> stu.getCode().equals(student.getCode()));
    }

    public boolean isSelected(IStudent student) {
        return mSelectedList.stream().anyMatch(stu -> stu.getCode().equals(student.getCode()));
    }

    public IStateLiveData<List<IStudent>> getSuggestions() {
        return new ConnectedStudents(mChatRepo.getAllConnections());
    }

    public List<IStudent> getSelectedList() {
        return mSelectedList;
    }

    public IStateLiveData<String> createGroupChat() {
        GroupChat groupChat = new GroupChat();
        groupChat.setName("TESTTTTTT");

        groupChat.setId(FirebaseStructureId.groupChat());
        groupChat.setAvatar(null);
        groupChat.setCreatedTime(System.currentTimeMillis() / 1000);

        Member_GroupChatSubCol me = new Member_GroupChatSubCol();
        me.setName(User.getInstance().getName());
        me.setRole(MemberRole.ADMIN);
        me.setId(User.getInstance().getStudentId());
        me.setAvatar(null);
        groupChat.getMembers().add(me);

        for (IStudent student : mSelectedList) {
            Member_GroupChatSubCol other = new Member_GroupChatSubCol();
            other.setName(student.getName());
            other.setAvatar(student.getAvatar());
            other.setId(student.getCode());
            other.setRole(MemberRole.MEMBER);
            groupChat.getMembers().add(other);
        }

        return mChatRepo.createGroupChat(groupChat);
    }

    static class ConnectedStudents extends StateMediatorLiveData<List<IStudent>> {

        private List<Connection> mConnections;
        private Map<String, IStudent> mStudentMap;

        public ConnectedStudents(@NonNull StateLiveData<List<Connection>> connections) {

            postLoading();

            addSource(connections, new Observer<StateModel<List<Connection>>>() {
                @Override
                public void onChanged(StateModel<List<Connection>> stateModel) {
                    switch (stateModel.getStatus()) {
                        case ERROR:
                            postError(stateModel.getError());
                            break;

                        case LOADING:
                            postLoading();
                            break;

                        case SUCCESS:
                            removeSource(connections);

                            mConnections = stateModel.getData();
                            mStudentMap = new HashMap<>();

                            boolean[] check = new boolean[mConnections.size()];

                            mConnections.forEach(connection -> {
                                String otherId = null;

                                for (String id : connection.getStudentIds()) {
                                    if (!id.equals(USER_ID)) {
                                        otherId = id;
                                        break;
                                    }
                                }

                                if (otherId != null) {
                                    StateMediatorLiveData<UserInfo> userInfo = (StateMediatorLiveData<UserInfo>) StudentRepository.getInstance().getStudentById(otherId);

                                    String finalOtherId = otherId;
                                    addSource(userInfo, new Observer<StateModel<UserInfo>>() {
                                        @Override
                                        public void onChanged(StateModel<UserInfo> stateModel) {
                                            switch (stateModel.getStatus()) {
                                                case ERROR:
                                                    postError(stateModel.getError());
                                                    break;

                                                case LOADING:
                                                    postLoading();
                                                    break;

                                                case SUCCESS:
                                                    removeSource(userInfo);

                                                    mStudentMap.put(finalOtherId, stateModel.getData());

                                                    if (mStudentMap.size() == mConnections.size()) {
                                                        postSuccess(new ArrayList<>(mStudentMap.values()));
                                                    }

                                            }
                                        }
                                    });
                                }

                            });

                            break;
                    }
                }
            });
        }

    }
}
