package vnu.uet.mobilecourse.assistant.repository.firebase;

import vnu.uet.mobilecourse.assistant.database.DAO.UserDAO;
import vnu.uet.mobilecourse.assistant.model.firebase.User;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;

public class FirebaseUserRepository {

    private static FirebaseUserRepository instance;
    private UserDAO dao;

    public static FirebaseUserRepository getInstance() {
        if (instance == null) {
            instance = new FirebaseUserRepository();
        }

        return instance;
    }

    public FirebaseUserRepository() {
        dao = new UserDAO();
    }

    public StateLiveData<User> search(String id) {
        return dao.read(id);
    }
}
