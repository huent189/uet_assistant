package vnu.uet.mobilecourse.assistant.repository.firebase;

import vnu.uet.mobilecourse.assistant.database.DAO.UserDAO;
import vnu.uet.mobilecourse.assistant.model.firebase.User;
import vnu.uet.mobilecourse.assistant.repository.cache.FirebaseUserCache;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class FirebaseUserRepository {

    private static FirebaseUserRepository instance;
    private UserDAO mDao;
    private FirebaseUserCache mCache;

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
}
