package vnu.uet.mobilecourse.assistant.viewmodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;

import com.google.firebase.firestore.util.Util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.model.firebase.MemberRole;
import vnu.uet.mobilecourse.assistant.model.firebase.Member_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.MessageToken;
import vnu.uet.mobilecourse.assistant.model.firebase.Message_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.repository.firebase.ChatRepository;
import vnu.uet.mobilecourse.assistant.storage.StorageAccess;
import vnu.uet.mobilecourse.assistant.util.FileUtils;
import vnu.uet.mobilecourse.assistant.util.StringConst;
import vnu.uet.mobilecourse.assistant.util.StringUtils;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class ChatRoomViewModel extends ViewModel {

    private ChatRepository mChatRepo = ChatRepository.getInstance();

    private static final String STUDENT_ID = User.getInstance().getStudentId();
    private static final String STUDENT_NAME = User.getInstance().getName();

    public IStateLiveData<List<Message_GroupChatSubCol>> getAllMessages(String roomId) {
        return mChatRepo.getMessages(roomId);
    }

    public IStateLiveData<GroupChat> getRoomInfo(String roomId) {
        return mChatRepo.getGroupChatInfo(roomId);
    }

    public IStateLiveData<String> markAsSeen(String roomId) {
        return mChatRepo.markRoomAsSeen(roomId);
    }

    public IStateLiveData<String> sendMessage(String roomId, String title, Message_GroupChatSubCol message, String[] memberIds, String[] tokens) {
        return mChatRepo.sendMessage(roomId, message, memberIds, tokens, title);
    }

    public IStateLiveData<MessageToken> getToken(String id) {
        return mChatRepo.getToken(id);
    }

    @SuppressLint("RestrictedApi")
    public IStateLiveData<String> sendAttachment(String roomId, String roomName, Uri uri, String[] memberIds, String[] tokens, Context context) {
        Message_GroupChatSubCol message = new Message_GroupChatSubCol();
        message.setId(Util.autoId());
        message.setTimestamp(System.currentTimeMillis() / 1000);
        message.setContent(uri.getLastPathSegment());
        message.setFromId(STUDENT_ID);
        message.setFromName(STUDENT_NAME);
        message.setContentType(FileUtils.getMimeType(context, uri));

        return new StorageAccess().uploadFileToGroupChat(roomId, uri, message, memberIds, tokens, roomName);
    }

    public IStateLiveData<String> connectAndSendMessage(String roomId, String otherName,
                                                        Message_GroupChatSubCol message, String[] memberIds, String[] tokens) {
        return new FirstMessageLiveData(roomId, message, otherName, memberIds, tokens);
    }

    private StateMediatorLiveData<GroupChat> createDirectedChat(String roomId, String otherName, String otherCode) {
        StateMediatorLiveData<GroupChat> liveData = new StateMediatorLiveData<>();
        liveData.postLoading();

        GroupChat groupChat = new GroupChat();

        groupChat.setId(roomId);
        groupChat.setCreatedTime(System.currentTimeMillis() / 1000);
        groupChat.setName(otherName);
        groupChat.setAvatar(null);

        Member_GroupChatSubCol me = new Member_GroupChatSubCol();
        me.setId(STUDENT_ID);
        me.setName(STUDENT_NAME);
        me.setRole(MemberRole.MEMBER);

        Member_GroupChatSubCol other = new Member_GroupChatSubCol();
        other.setId(otherCode);
        other.setName(otherName);
        other.setRole(MemberRole.MEMBER);

        groupChat.getMembers().add(me);
        groupChat.getMembers().add(other);

        liveData = mChatRepo.createGroupChat(groupChat);

        return liveData;
    }

    @SuppressLint("RestrictedApi")
    public Message_GroupChatSubCol generateTextMessage(String content, String[] memberIds) {
        Message_GroupChatSubCol message = new Message_GroupChatSubCol();

        message.setId(Util.autoId());

        message.setFromId(User.getInstance().getStudentId());
        message.setFromName(User.getInstance().getName());

        message.setContentType(FileUtils.MIME_TEXT);

        message.setTimestamp(System.currentTimeMillis() / 1000);

        List<String> mentions = parseMentions(content, memberIds);

        content = refactorContent(content, mentions);
        message.setContent(content);

        mentions = mentions.stream()
                .map(mention -> {
                    String out = mention;
                    if (out.charAt(0) == StringConst.AT_SIGN) {
                        out = out.substring(1);
                    }
                    return out;
                })
                .distinct()
                .collect(Collectors.toList());
        message.setMentions(mentions);

        return message;
    }

    private String refactorContent(String content, List<String> mentions) {
        String refactor = content;

        for (String mention : mentions) {
            refactor = refactor.replaceAll(mention + "\\b", "<b>" + mention + "</b>");
        }

        return refactor;
    }

    private List<String> parseMentions(String content, String[] memberIds) {
        List<String> mentions = StringUtils.getAllMatches(content, "@1[0-9]{7}\\b");

        mentions.removeIf(mention -> {
            boolean isMember = false;

            for (String memberId : memberIds) {
                if (mention.equals(StringConst.AT_SIGN + memberId)) {
                    isMember = true;
                    break;
                }
            }

            return !isMember;
        });

        return mentions;
    }

    class FirstMessageLiveData extends StateMediatorLiveData<String> {

        FirstMessageLiveData(String roomId, Message_GroupChatSubCol message, String otherName, String[] memberIds, String[] tokens) {
            postLoading();

//            String[] memberIds = members.keySet().toArray(new String[0]);
            String otherId = findOtherId(memberIds);

            StateMediatorLiveData<GroupChat> createLiveData = createDirectedChat(roomId, otherName, otherId);

            addSource(createLiveData, new Observer<StateModel<GroupChat>>() {
                @Override
                public void onChanged(StateModel<GroupChat> stateModel) {
                    switch (stateModel.getStatus()) {
                        case LOADING:
                            postLoading();
                            break;

                        case ERROR:
                            postError(stateModel.getError());
                            break;

                        case SUCCESS:
                            postSuccess(CONNECTED_MSG);
                            message.setTimestamp(message.getTimestamp() + 1);

                            addSource(mChatRepo.sendMessage(roomId, message, memberIds, tokens, User.getInstance().getName()), new Observer<StateModel<String>>() {
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

        private String findOtherId(String[] memberIds) {
            String otherId = null;
            for (String id : memberIds) {
                if (!id.equals(STUDENT_ID)) {
                    otherId = id;
                    break;
                }
            }

            return otherId;
        }
    }

    public static final String CONNECTED_MSG = "Hai bạn đã được kết nối";
}
