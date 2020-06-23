package vnu.uet.mobilecourse.assistant.viewmodel;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.IStudent;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.model.firebase.MemberRole;
import vnu.uet.mobilecourse.assistant.model.firebase.Member_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.UserInfo;
import vnu.uet.mobilecourse.assistant.repository.firebase.ChatRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.StudentRepository;
import vnu.uet.mobilecourse.assistant.util.FirebaseStructureId;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public class AddMemberViewModel extends ViewModel {

    private StudentRepository mStudentRepo = StudentRepository.getInstance();
    private ChatRepository mChatRepo = ChatRepository.getInstance();

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
}
