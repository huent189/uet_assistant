package vnu.uet.mobilecourse.assistant.viewmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.IStudent;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.Connection;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
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

public class AddGroupChatViewModel extends ViewModel {

    private StudentRepository mStudentRepo = StudentRepository.getInstance();
    private ChatRepository mChatRepo = ChatRepository.getInstance();

    private static final String USER_ID = User.getInstance().getStudentId();

    private MutableLiveData<List<IStudent>> mSelectedList = new MutableLiveData<>();

    public AddGroupChatViewModel() {
        clearData();
    }

    public void clearData() {
        mSelectedList.postValue(new ArrayList<>());
    }

    public IStateLiveData<UserInfo> searchStudent(String id) {
        return mStudentRepo.getStudentById(id);
    }

    public synchronized void addMember(IStudent student) {
        List<IStudent> list = mSelectedList.getValue();

        assert list != null;

        boolean isExist = list.stream()
                .anyMatch(stu -> stu.getCode().equals(student.getCode()));

        if (!isExist) list.add(student);

        mSelectedList.postValue(list);
    }

    public synchronized void removeMember(IStudent student) {
        List<IStudent> list = mSelectedList.getValue();
        assert list != null;
        list.removeIf(stu -> stu.getCode().equals(student.getCode()));

        mSelectedList.postValue(list);
    }

    public LiveData<Boolean> isSelected(IStudent student) {
        return new SelectedState(mSelectedList, student);
    }

    public IStateLiveData<List<IStudent>> getSuggestions() {
        return new ConnectedStudents(mChatRepo.getAllConnections());
    }

    public LiveData<List<IStudent>> getSelectedList() {
        return mSelectedList;
    }

    public IStateLiveData<List<Member_GroupChatSubCol>> addMemberToExistRoom(GroupChat room, List<IStudent> students) {
        List<Member_GroupChatSubCol> members = students.stream()
                .map(this::convertToMember)
                .collect(Collectors.toList());

        String[] memberIds = room.getMembers().stream()
                .map(Member_GroupChatSubCol::getId)
                .toArray(String[]::new);

        return mChatRepo.addMember(room.getId(), memberIds, members);
    }

    private Member_GroupChatSubCol convertToMember(IStudent student) {
        Member_GroupChatSubCol member = new Member_GroupChatSubCol();
        member.setId(student.getCode());
        member.setAvatar(student.getAvatar());
        member.setName(student.getName());
        member.setRole(MemberRole.MEMBER);

        return member;
    }

    public IStateLiveData<GroupChat> createGroupChat(String title) {
        GroupChat groupChat = new GroupChat();
        groupChat.setName(title);

        groupChat.setId(FirebaseStructureId.groupChat());
        groupChat.setAvatar(null);
        groupChat.setCreatedTime(System.currentTimeMillis() / 1000);

        Member_GroupChatSubCol me = new Member_GroupChatSubCol();
        me.setName(User.getInstance().getName());
        me.setRole(MemberRole.ADMIN);
        me.setId(User.getInstance().getStudentId());
        me.setAvatar(null);
        groupChat.getMembers().add(me);

        List<IStudent> students = mSelectedList.getValue();
        assert students != null;
        for (IStudent student : students) {
            Member_GroupChatSubCol other = new Member_GroupChatSubCol();
            other.setName(student.getName());
            other.setAvatar(student.getAvatar());
            other.setId(student.getCode());
            other.setRole(MemberRole.MEMBER);
            groupChat.getMembers().add(other);
        }

        return mChatRepo.createGroupChat(groupChat);
    }

    static class SelectedState extends MediatorLiveData<Boolean> {

        SelectedState(@NonNull LiveData<List<IStudent>> listLiveData, IStudent student) {
            postValue(Boolean.FALSE);

            addSource(listLiveData, new Observer<List<IStudent>>() {
                @Override
                public void onChanged(List<IStudent> students) {
                    boolean selected = students.stream()
                            .anyMatch(stu -> stu.getCode().equals(student.getCode()));

                    postValue(selected);
                }
            });
        }
    }

    static class ConnectedStudents extends StateMediatorLiveData<List<IStudent>> {

        private List<Connection> mConnections;
        private Map<String, IStudent> mStudentMap;

        ConnectedStudents(@NonNull StateLiveData<List<Connection>> connections) {

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
                            mConnections.removeIf(connection -> {
                                List<String> ids = connection.getStudentIds();

                                if (ids.size() != 2)
                                    return true;

                                return ids.get(0).equals(ids.get(1));
                            });

                            mStudentMap = new HashMap<>();

                            mConnections.forEach(connection -> {
                                String otherId = null;

                                for (String id : connection.getStudentIds()) {
                                    if (!id.equals(USER_ID)) {
                                        otherId = id;
                                        break;
                                    }
                                }

                                if (otherId != null) {
                                    StateMediatorLiveData<UserInfo> userInfo = (StateMediatorLiveData<UserInfo>)
                                            StudentRepository.getInstance().getStudentById(otherId);

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
