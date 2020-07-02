package vnu.uet.mobilecourse.assistant.repository.firebase;

import java.util.List;

import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.Member_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.Message_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;

public interface IChatRepository {

    /**
     * Retrieve all group chat which current user participate
     * @return state live data interface contains list of group chat
     */
    IStateLiveData<List<GroupChat_UserSubCol>> getAllUserGroupChats();

    /**
     * Retrieve information of selected group chat
     * (name/title, created time, avatar)
     * and its sub collection members info,
     * such as student name, student id, avatar
     *
     * @param groupId of given group chat
     * @return state live data interface contains all above info
     */
    IStateLiveData<GroupChat> getGroupChatInfo(String groupId);

    /**
     * Retrieve all messages belong to given group chat
     *
     * @param groupId of given group chat
     * @return state live data interface
     *      contains all messages in group chat
     */
    IStateLiveData<List<Message_GroupChatSubCol>> getMessages(String groupId);

    /**
     * Send a message in given group chat
     *
     * @param groupId of given group chat
     * @param message model contains message info
     *
     * @return state live data interface
     *      contains response state of this operation
     */
    StateMediatorLiveData<String> sendMessage(String groupId, Message_GroupChatSubCol message, String[] memberIds, String[] tokens, String groupName);

    /**
     * Request to create a new group chat
     *
     * @param groupChat contains all needed information
     * @return state live data interface
     *      contains response state of this operation
     */
    IStateLiveData<GroupChat> createGroupChat(GroupChat groupChat);

    /**
     * Request to add a new member into group chat
     *
     * @param roomId of group chat
     * @param memberIds previous member ids
     * @param members a list of new member
     * @return state live data interface
     *      contains response state of this operation
     */
    IStateLiveData<List<Member_GroupChatSubCol>> addMember(String roomId, String[] memberIds,
                                                           List<Member_GroupChatSubCol> members);

    /**
     * Request to remove a new member from group chat
     *
     * @param roomId of group chat
     * @param memberIds previous member ids
     * @param memberId of member need to remove
     * @return state live data interface
     *      contains response state of this operation
     */
    IStateLiveData<String> removeMember(String roomId, String[] memberIds, String memberId);

    /**
     * Request to change room chat title
     *
     * @param roomId of group chat
     * @param memberIds previous member ids
     * @param title of room chat
     * @return state live data interface
     *      contains response state of this operation
     */
    IStateLiveData<String> changeTitle(String roomId, String[] memberIds, String title);
}
