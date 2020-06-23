package vnu.uet.mobilecourse.assistant.repository.firebase;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import vnu.uet.mobilecourse.assistant.database.DAO.GroupChatDAO;
import vnu.uet.mobilecourse.assistant.database.DAO.GroupChat_UserSubColDAO;
import vnu.uet.mobilecourse.assistant.database.DAO.Member_GroupChatSubColDAO;
import vnu.uet.mobilecourse.assistant.database.DAO.Message_GroupChatSubColDAO;
import vnu.uet.mobilecourse.assistant.exception.DocumentNotFoundException;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.Member_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.Message_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class ChatRepository implements IChatRepository {

    private GroupChat_UserSubColDAO mUserGroupChatDAO;
    private GroupChatDAO mGroupChatDAO;

    private static ChatRepository instance;

    public static ChatRepository getInstance() {
        if (instance == null) {
            instance = new ChatRepository();
        }

        return instance;
    }

    private ChatRepository() {
        mUserGroupChatDAO = new GroupChat_UserSubColDAO();
        mGroupChatDAO = new GroupChatDAO();
    }

    @Override
    public IStateLiveData<List<GroupChat_UserSubCol>> getAllUserGroupChats() {
        return mUserGroupChatDAO.readAll();
    }

    @Override
    public IStateLiveData<GroupChat> getGroupChatInfo(String groupId) {
        StateLiveData<GroupChat> groupChat = mGroupChatDAO.read(groupId);
        StateLiveData<List<Member_GroupChatSubCol>> members = new Member_GroupChatSubColDAO(groupId).readAll();
        StateMediatorLiveData<GroupChat_UserSubCol> userGroupChat = mUserGroupChatDAO.read(groupId);

        return new MergeGroupChat(groupChat, userGroupChat, members);
    }

    @Override
    public IStateLiveData<List<Message_GroupChatSubCol>> getMessages(String groupId) {
        Message_GroupChatSubColDAO mMessageDAO = new Message_GroupChatSubColDAO(groupId);
        return mMessageDAO.readAll();
    }

    @Override
    public StateMediatorLiveData<String> sendMessage(String groupId, Message_GroupChatSubCol message, String[] memberIds) {
        StateLiveData<Message_GroupChatSubCol> addMessageState= new Message_GroupChatSubColDAO(groupId).add(message.getId(), message);

        return new SendMessageState(groupId, addMessageState, memberIds);
    }

    @Override
    public StateMediatorLiveData<String> createGroupChat(GroupChat groupChat) {
        StateLiveData<GroupChat> createGroupState = mGroupChatDAO.add(groupChat.getId(), groupChat);
        StateLiveData<String> addGroup = mUserGroupChatDAO.addGroupChat(groupChat);
        StateLiveData<String> addMember = mGroupChatDAO.addMembers(groupChat.getId(), groupChat.getMembers());

        return new CreateGroupChatState(createGroupState, addMember, addGroup);
    }

    static class MergeGroupChat extends StateMediatorLiveData<GroupChat> {

        private boolean roomSuccess = false;
        private boolean memberSuccess = false;
        private boolean subColSuccess = false;

        private GroupChat room;
        private GroupChat_UserSubCol userGroupChat;
        private List<Member_GroupChatSubCol> members;

        MergeGroupChat(@NonNull StateLiveData<GroupChat> groupChat,
                       @NonNull StateMediatorLiveData<GroupChat_UserSubCol> userGroupChat,
                       @NonNull StateLiveData<List<Member_GroupChatSubCol>> members) {

            postLoading();

            addSource(groupChat, new Observer<StateModel<GroupChat>>() {
                @Override
                public void onChanged(StateModel<GroupChat> stateModel) {
                    switch (stateModel.getStatus()) {
                        case LOADING:
                            roomSuccess = false;
                            postLoading();

                            break;

                        case ERROR:
                            roomSuccess = false;
                            postError(stateModel.getError());

                            break;

                        case SUCCESS:
                            roomSuccess = true;
                            setRoom(stateModel.getData());

                            if (roomSuccess && memberSuccess) {
                                GroupChat combine = combineData();
                                postSuccess(combine);
                            }
                    }
                }
            });

            addSource(userGroupChat, new Observer<StateModel<GroupChat_UserSubCol>>() {
                @Override
                public void onChanged(StateModel<GroupChat_UserSubCol> stateModel) {
                    switch (stateModel.getStatus()) {
                        case LOADING:
                            subColSuccess = false;
                            postLoading();

                            break;

                        case ERROR:
                            subColSuccess = false;
                            postError(stateModel.getError());

                            break;

                        case SUCCESS:
                            subColSuccess = true;
                            setUserGroupChat(stateModel.getData());

                            if (roomSuccess && memberSuccess) {
                                GroupChat combine = combineData();
                                postSuccess(combine);
                            }
                    }
                }
            });

            addSource(members, new Observer<StateModel<List<Member_GroupChatSubCol>>>() {
                @Override
                public void onChanged(StateModel<List<Member_GroupChatSubCol>> stateModel) {
                    switch (stateModel.getStatus()) {
                        case LOADING:
                            memberSuccess = false;
                            postLoading();

                            break;

                        case ERROR:
                            memberSuccess = false;
                            postError(stateModel.getError());

                            break;

                        case SUCCESS:
                            memberSuccess = true;
                            setMembers(stateModel.getData());

                            if (roomSuccess && memberSuccess) {
                                GroupChat combine = combineData();
                                postSuccess(combine);
                            }
                    }
                }

            });
        }

        private GroupChat combineData() {
            room.getMembers().clear();
            room.setAvatar(userGroupChat.getAvatar());
            room.setName(userGroupChat.getName());
            room.getMembers().addAll(members);

            return room;
        }

        private void setMembers(List<Member_GroupChatSubCol> members) {
            this.members = members;
        }

        private void setUserGroupChat(GroupChat_UserSubCol userGroupChat) {
            this.userGroupChat = userGroupChat;
        }

        private void setRoom(GroupChat room) {
            this.room = room;
        }
    }

    static class CreateGroupChatState extends StateMediatorLiveData<String> {

        private boolean createGroupState = false;
        private boolean addMemberToGroupState = false;
        private boolean addGroupToMemberState = false;

        CreateGroupChatState(@NonNull StateLiveData<GroupChat> createGroupLiveData,
                             @NonNull StateLiveData<String> addMemberLiveData,
                             @NonNull StateLiveData<String> addGroupLiveData) {
            super(new StateModel<>(StateStatus.LOADING));
            addSource(createGroupLiveData, stateModel -> {
                switch (stateModel.getStatus()) {
                    case ERROR:
                        createGroupState = false;
                        postError(stateModel.getError());
                        break;
                    case SUCCESS:
                        createGroupState = true;
                        break;
                    case LOADING:
                        createGroupState = false;
                        postLoading();
                        break;
                }
                if (createGroupState && addMemberToGroupState && addGroupToMemberState) {
                    postSuccess("create group success!");
                }
            });

            addSource(addMemberLiveData, stateModel -> {
                switch (stateModel.getStatus()) {
                    case ERROR:
                        addMemberToGroupState = false;
                        postError(stateModel.getError());
                        break;
                    case SUCCESS:
                        addMemberToGroupState = true;
                        break;
                    case LOADING:
                        addMemberToGroupState = false;
                        postLoading();
                        break;
                }
                if (createGroupState && addMemberToGroupState && addGroupToMemberState) {
                    postSuccess("create group success!");
                }
            });

            addSource(addGroupLiveData, stateModel -> {
                switch (stateModel.getStatus()) {
                    case ERROR:
                        addGroupToMemberState = false;
                        postError(stateModel.getError());
                        break;
                    case SUCCESS:
                        addGroupToMemberState = true;
                        break;
                    case LOADING:
                        addGroupToMemberState = false;
                        postLoading();
                        break;
                }
                if (createGroupState && addMemberToGroupState && addGroupToMemberState) {
                    postSuccess("create group success!");
                }
            });
        }
    }

    static class SendMessageState extends StateMediatorLiveData<String>{
        SendMessageState(String groupId, @NonNull StateLiveData<Message_GroupChatSubCol> addMessageToGroup,
                         String[] memberIds) {

            addSource(addMessageToGroup, stateModel -> {
                switch (stateModel.getStatus()) {
                    case LOADING:
                        postLoading();
                        break;
                    case ERROR:
                        postError(stateModel.getError());
                        break;
                    case SUCCESS:
                        StateLiveData<String> updateLastMessage = new GroupChat_UserSubColDAO()
                                .updateLastMessage(groupId, memberIds, stateModel.getData());

                        addSource(updateLastMessage, updateLastMessageStateModel -> {
                            switch (updateLastMessageStateModel.getStatus()){
                                case ERROR:
                                    postError(updateLastMessageStateModel.getError());
                                    break;
                                case LOADING:
                                    postLoading();
                                    break;
                                case SUCCESS:
                                    postSuccess("send message success");
                                    break;
                            }
                        });
                }
            });
        }

    }

    @Deprecated
    static class ConnectedCheckingLiveData extends StateMediatorLiveData<Boolean> {

        public ConnectedCheckingLiveData(StateLiveData<GroupChat_UserSubCol> readLiveData) {
            postLoading();

            addSource(readLiveData, new Observer<StateModel<GroupChat_UserSubCol>>() {
                @Override
                public void onChanged(StateModel<GroupChat_UserSubCol> stateModel) {
                    switch (stateModel.getStatus()) {
                        case LOADING:
                            postLoading();
                            break;

                        case ERROR:
                            Exception exception = stateModel.getError();

                            if (exception instanceof DocumentNotFoundException) {
                                postSuccess(Boolean.FALSE);
                            } else {
                                postError(exception);
                            }

                            break;

                        case SUCCESS:
                            postSuccess(Boolean.TRUE);
                            break;
                    }
                }
            });
        }
    }
}