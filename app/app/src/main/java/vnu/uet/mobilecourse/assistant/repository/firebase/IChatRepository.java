package vnu.uet.mobilecourse.assistant.repository.firebase;

import java.util.Date;
import java.util.List;
import java.util.Map;

import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.Message_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.model.firebase.TodoList;
import vnu.uet.mobilecourse.assistant.model.todo.DailyTodoList;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

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
     * @param id of given group chat
     * @return state live data interface contains all above info
     */
    IStateLiveData<GroupChat> getGroupChatInfo(String id);

    /**
     * Retrieve all messages belong to given group chat
     *
     * @param id of given group chat
     * @return state live data interface
     *      contains all messages in group chat
     */
    IStateLiveData<List<Message_GroupChatSubCol>> getMessages(String id);

    /**
     * Send a message in given group chat
     *
     * @param groupId of given group chat
     * @param message model contains message info
     *
     * @return state live data interface
     *      contains response state of this operation
     */
    IStateLiveData<String> sendMessage(String groupId, Message_GroupChatSubCol message);
}
