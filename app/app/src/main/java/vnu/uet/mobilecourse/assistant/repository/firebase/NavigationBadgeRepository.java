package vnu.uet.mobilecourse.assistant.repository.firebase;

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

    public IStateLiveData<Long> getNonSeenChats() {
        IStateLiveData<List<GroupChat_UserSubCol>> liveData = mChatRepo.getAllUserGroupChats();
        IStateLiveData<Long> counter = new StateMediatorLiveData<>(new StateModel<>(StateStatus.LOADING));

        if (liveData instanceof StateLiveData) {
            StateLiveData<List<GroupChat_UserSubCol>> listLiveData = (StateLiveData<List<GroupChat_UserSubCol>>) liveData;
            counter = new NonSeenRoomCounter(listLiveData);
        }

        return counter;
    }

    public StateLiveData<String> increaseNewNotifications() {
        return mUserRepo.increaseNotifications();
    }

    public StateLiveData<String> seeAllNotifications() {
        Map<String, Object> changes = new HashMap<>();
        changes.put("newNotifications", 0);

        return mUserRepo.modify(changes);
    }

    static class NewNotificationCounter extends StateMediatorLiveData<Integer> {

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
                        int counter = user.getNewNotifications();
                        postSuccess(counter);
//                        mNewNotificationCounter = counter;

                        break;
                }
            });
        }
    }

    public static class NonSeenRoomCounter extends StateMediatorLiveData<Long> {

        NonSeenRoomCounter(StateLiveData<List<GroupChat_UserSubCol>> listLiveData) {
            addSource(listLiveData, stateModel -> {
                switch (stateModel.getStatus()) {
                    case LOADING:
                        postLoading();
                        break;

                    case ERROR:
                        postError(stateModel.getError());
                        break;

                    case SUCCESS:
                        List<GroupChat_UserSubCol> rooms = stateModel.getData();
                        long counter = rooms.stream()
                                .filter(room -> !room.isSeen())
                                .count();
                        postSuccess(counter);

                        break;
                }
            });
        }
    }
}
