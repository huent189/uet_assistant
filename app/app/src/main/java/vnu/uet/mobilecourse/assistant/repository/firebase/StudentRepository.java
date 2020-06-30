package vnu.uet.mobilecourse.assistant.repository.firebase;

import vnu.uet.mobilecourse.assistant.database.DAO.UserInfoDAO;
import vnu.uet.mobilecourse.assistant.exception.DocumentNotFoundException;
import vnu.uet.mobilecourse.assistant.model.firebase.User;
import vnu.uet.mobilecourse.assistant.model.firebase.UserInfo;
import vnu.uet.mobilecourse.assistant.repository.cache.StudentCache;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class StudentRepository {

    private static StudentRepository instance;
    private final FirebaseUserRepository mUserRepo;
    private final UserInfoDAO mUserInfoDAO;
    private final StudentCache mCache;

    public static StudentRepository getInstance() {
        if (instance == null) {
            instance = new StudentRepository();
        }

        return instance;
    }

    private StudentRepository() {
        mUserRepo = FirebaseUserRepository.getInstance();
        mUserInfoDAO = new UserInfoDAO();
        mCache = new StudentCache();
    }

    public IStateLiveData<UserInfo> getStudentById(String id) {
        if (mCache.containsKey(id)) {
            return mCache.get(id);
        } else {
            StateLiveData<User> userLiveData = mUserRepo.search(id);
            StateLiveData<UserInfo> infoLiveData = mUserInfoDAO.read(id);
            IStateLiveData<UserInfo> liveData = new MergeStudentStateLiveData(userLiveData, infoLiveData);
            mCache.put(id, liveData);
            return liveData;
        }
    }

    static class MergeStudentStateLiveData extends StateMediatorLiveData<UserInfo> {

        private User userModel;
        private UserInfo infoModel;
        private boolean userDone;
        private boolean infoDone;

        MergeStudentStateLiveData(StateLiveData<User> user, StateLiveData<UserInfo> info) {
            super(new StateModel<>(StateStatus.LOADING));

            addSource(info, stateModel -> {
                switch (stateModel.getStatus()) {
                    case LOADING:
                        infoDone = false;
                        postLoading();
                        break;

                    case ERROR:
                        infoDone = false;
                        postError(stateModel.getError());
                        break;

                    case SUCCESS:
                        infoDone = true;
                        infoModel = stateModel.getData();
                        break;
                }

                if (userDone && infoDone) {
                    UserInfo combine = combineData();
                    postSuccess(combine);
                }
            });

            addSource(user, stateModel -> {
                switch (stateModel.getStatus()) {
                    case LOADING:
                        userDone = false;
                        postLoading();
                        break;

                    case ERROR:
                        Exception e = stateModel.getError();

                        if (e instanceof DocumentNotFoundException) {
                            userModel = null;
                            userDone = true;
                        } else {
                            userDone = false;
                            postError(e);
                        }

                        break;

                    case SUCCESS:
                        userDone = true;
                        userModel = stateModel.getData();

                        break;
                }

                if (userDone && infoDone) {
                    UserInfo combine = combineData();
                    postSuccess(combine);
                }
            });
        }

        private UserInfo combineData() {
            UserInfo clone = infoModel.clone();

            if (userModel == null) {
                clone.setActive(false);
                clone.setAvatar(null);
            } else {
                clone.setActive(true);
//                clone.setAvatar(userModel.getAvatar());
            }

            return clone;
        }
    }
}
