package vnu.uet.mobilecourse.assistant.repository.firebase;

import vnu.uet.mobilecourse.assistant.database.DAO.UserInfoDAO;
import vnu.uet.mobilecourse.assistant.exception.DocumentNotFoundException;
import vnu.uet.mobilecourse.assistant.model.firebase.User;
import vnu.uet.mobilecourse.assistant.model.firebase.UserInfo;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class StudentRepository {

    private static StudentRepository instance;
    private FirebaseUserRepository userRepo;
    private UserInfoDAO userInfoDAO;

    public static StudentRepository getInstance() {
        if (instance == null) {
            instance = new StudentRepository();
        }

        return instance;
    }

    public StudentRepository() {
        userRepo = FirebaseUserRepository.getInstance();
        userInfoDAO = new UserInfoDAO();
    }

    public IStateLiveData<UserInfo> getStudentById(String id) {
        StateLiveData<User> userLiveData = userRepo.search(id);
        StateLiveData<UserInfo> infoLiveData = userInfoDAO.read(id);
        return new MergeStudentStateLiveData(userLiveData, infoLiveData);
    }

    public static class MergeStudentStateLiveData extends StateMediatorLiveData<UserInfo> {

        private User userModel;
        private UserInfo infoModel;
        private boolean userDone;
        private boolean infoDone;

        public MergeStudentStateLiveData(StateLiveData<User> user, StateLiveData<UserInfo> info) {
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

                        if (userDone && infoDone) {
                            UserInfo combine = combineData();
                            postSuccess(combine);
                        }
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

                        if (userDone && infoDone) {
                            UserInfo combine = combineData();
                            postSuccess(combine);
                        }
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
                clone.setAvatar(userModel.getAvatar());
            }

            return clone;
        }
    }
}
