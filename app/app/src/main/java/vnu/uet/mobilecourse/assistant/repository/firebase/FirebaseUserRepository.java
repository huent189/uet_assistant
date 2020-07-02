package vnu.uet.mobilecourse.assistant.repository.firebase;

import java.util.HashMap;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import vnu.uet.mobilecourse.assistant.database.DAO.OnlineStateDAO;
import vnu.uet.mobilecourse.assistant.database.DAO.UserDAO;
import vnu.uet.mobilecourse.assistant.model.firebase.OnlineState;
import vnu.uet.mobilecourse.assistant.model.firebase.User;
import vnu.uet.mobilecourse.assistant.repository.cache.FirebaseUserCache;
import vnu.uet.mobilecourse.assistant.util.NetworkChangeReceiver;
import vnu.uet.mobilecourse.assistant.util.NetworkUtils;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class FirebaseUserRepository {

    private static FirebaseUserRepository instance;
    private UserDAO mDao;
    private FirebaseUserCache mCache;

    private OnlineStateDAO mOnlineDao;

    private static final String STUDENT_ID = vnu.uet.mobilecourse.assistant.model.User
            .getInstance().getStudentId();

    public static FirebaseUserRepository getInstance() {
        if (instance == null) {
            instance = new FirebaseUserRepository();
        }

        return instance;
    }

    private FirebaseUserRepository() {
        mDao = new UserDAO();
        mCache = new FirebaseUserCache();
        mOnlineDao = new OnlineStateDAO();
    }

    public StateLiveData<User> search(String id) {
        StateLiveData<User> liveData = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));

        if (mCache.containsKey(id)) {
            if (mCache.get(id) instanceof StateLiveData) {
                liveData = (StateLiveData<User>) mCache.get(id);
            }
        } else {
            liveData = mDao.read(id);
            mCache.put(id, liveData);
        }

        return liveData;
    }

    public StateLiveData<String> modify(Map<String, Object> changes) {
        return mDao.update(STUDENT_ID, changes);
    }

    public StateLiveData<String> increaseNotifications() {
        return mDao.increaseNotifications(STUDENT_ID);
    }

    public IStateLiveData<Boolean> onlineState(String id) {
        return new OnlineStateLiveData(mOnlineDao.read(id), NetworkChangeReceiver.getInstance().getLiveData());
    }

    public IStateLiveData<String> changeOnlineState(boolean state) {
        Map<String, Object> change = new HashMap<>();
        change.put("state", state);
        return mOnlineDao.update(STUDENT_ID, state);
    }

    public IStateLiveData<User> add(User user) {
        return mDao.add(user);
    }

    public static class OnlineStateLiveData extends StateMediatorLiveData<Boolean> {

        private int networkStatus;
        private boolean online;

        public OnlineStateLiveData(StateLiveData<OnlineState> onlineState, LiveData<Integer> network) {
            addSource(onlineState, new Observer<StateModel<OnlineState>>() {
                @Override
                public void onChanged(StateModel<OnlineState> stateModel) {
                    switch (stateModel.getStatus()) {
                        case LOADING:
                            postLoading();
                            break;

                        case ERROR:
                            postError(stateModel.getError());
                            break;

                        case SUCCESS:
                            online = stateModel.getData().isState();
                            postSuccess(isOnline());
                    }
                }
            });

            addSource(network, new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    networkStatus = integer;
                    postSuccess(isOnline());
                }
            });
        }

        private boolean isOnline() {
            return online && networkStatus != NetworkUtils.STATUS_NOT_CONNECTED;
        }
    }
}
