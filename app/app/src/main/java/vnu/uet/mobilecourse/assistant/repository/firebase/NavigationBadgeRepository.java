package vnu.uet.mobilecourse.assistant.repository.firebase;

import androidx.lifecycle.Observer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.User;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class NavigationBadgeRepository {

    private static NavigationBadgeRepository instance;

    private FirebaseUserRepository mUserRepo;
    private ChatRepository mChatRepo;

    private static final String STUDENT_ID = vnu.uet.mobilecourse.assistant.model.User
            .getInstance().getStudentId();

    public static NavigationBadgeRepository getInstance() {
        if (instance == null) {
            instance = new NavigationBadgeRepository();
        }

        return instance;
    }

    private NavigationBadgeRepository() {
        mUserRepo = FirebaseUserRepository.getInstance();
        mChatRepo = ChatRepository.getInstance();
    }

    public StateMediatorLiveData<Integer> getNewNotifications() {
        StateLiveData<User> liveData = mUserRepo.search(STUDENT_ID);

        return new NewNotificationCounter(liveData);
    }

    public StateMediatorLiveData<Integer> getUnseenChats() {
        IStateLiveData<List<GroupChat_UserSubCol>> liveData = mChatRepo.getAllUserGroupChats();
        StateMediatorLiveData<Integer> counter = new StateMediatorLiveData<>(new StateModel<>(StateStatus.ERROR));

        if (liveData instanceof StateLiveData) {
            counter = new UnseenGroupChatCounter((StateLiveData<List<GroupChat_UserSubCol>>) liveData);
        }

        return counter;
    }

    public StateLiveData<String> seeAllNotifications() {
        Map<String, Object> changes = new HashMap<>();
        changes.put("newNotifications", 0);

        return mUserRepo.modify(changes);
    }

    public static class NewNotificationCounter extends StateMediatorLiveData<Integer> {

        public NewNotificationCounter(StateLiveData<User> userLiveData) {
            postLoading();

            addSource(userLiveData, stateModel -> {
                switch (stateModel.getStatus()) {
                    case LOADING:
                        postLoading();
                        break;

                    case ERROR:
                        postError(stateModel.getError());
                        break;

                    case SUCCESS:
                        User user = stateModel.getData();
                        postSuccess(user.getNewNotifications());
                        break;
                }
            });
        }
    }

    public static class UnseenGroupChatCounter extends StateMediatorLiveData<Integer> {

        public UnseenGroupChatCounter(StateLiveData<List<GroupChat_UserSubCol>> groupChats) {
            postLoading();

            addSource(groupChats, stateModel -> {
                switch (stateModel.getStatus()) {
                    case LOADING:
                        postLoading();
                        break;

                    case ERROR:
                        postError(stateModel.getError());
                        break;

                    case SUCCESS:
                        List<GroupChat_UserSubCol> groupChats1 = stateModel.getData();

                        long counter = groupChats1.stream()
                                .filter(groupChat -> !groupChat.isSeen())
                                .count();

                        postSuccess((int) counter);

                        break;
                }
            });
        }
    }
}
