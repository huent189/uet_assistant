package vnu.uet.mobilecourse.assistant.repository.firebase;

import java.util.Map;

import vnu.uet.mobilecourse.assistant.database.DAO.UserDAO;
import vnu.uet.mobilecourse.assistant.model.firebase.User;
import vnu.uet.mobilecourse.assistant.repository.cache.FirebaseUserCache;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class FirebaseUserRepository {

    private static FirebaseUserRepository instance;
    private UserDAO mDao;
    private FirebaseUserCache mCache;

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

    public IStateLiveData<User> add(User user) {
        return mDao.add(user);
    }
}
