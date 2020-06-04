package vnu.uet.mobilecourse.assistant.repository.firebase;

import vnu.uet.mobilecourse.assistant.database.DAO.UserDAO;
import vnu.uet.mobilecourse.assistant.model.firebase.User;
import vnu.uet.mobilecourse.assistant.repository.cache.FirebaseUserCache;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class FirebaseUserRepository {

    private static FirebaseUserRepository instance;
    private UserDAO dao;
    private FirebaseUserCache cache;

    public static FirebaseUserRepository getInstance() {
        if (instance == null) {
            instance = new FirebaseUserRepository();
        }

        return instance;
    }

    public FirebaseUserRepository() {
        dao = new UserDAO();
        cache = new FirebaseUserCache();
    }

    public StateLiveData<User> search(String id) {
        StateLiveData<User> liveData = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));

        if (cache.containsKey(id)) {
            if (cache.get(id) instanceof StateLiveData) {
                liveData = (StateLiveData<User>) cache.get(id);
            }
        } else {
            liveData = dao.read(id);
            cache.put(id, liveData);
        }

        return liveData;
    }
}
