package vnu.uet.mobilecourse.assistant.repository.firebase;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import vnu.uet.mobilecourse.assistant.database.DAO.ParticipantDAO;
import vnu.uet.mobilecourse.assistant.exception.DocumentNotFoundException;
import vnu.uet.mobilecourse.assistant.model.firebase.Participant_CourseSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.User;
import vnu.uet.mobilecourse.assistant.model.firebase.UserInfo;
import vnu.uet.mobilecourse.assistant.repository.cache.ParticipantCache;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class ParticipantRepository {

    private static ParticipantRepository instance;

    private ParticipantCache cache;

    private ParticipantDAO dao;

    private FirebaseUserRepository userRepo;

    public static ParticipantRepository getInstance() {
        if (instance == null) {
            instance = new ParticipantRepository();
        }

        return instance;
    }

    public ParticipantRepository() {
        cache = new ParticipantCache();
        dao = new ParticipantDAO();
        userRepo = FirebaseUserRepository.getInstance();
    }

    /**
     * Get all participants in a specific course
     *
     * @param courseId of course
     * @return state live data contains result
     */
    public IStateLiveData<List<Participant_CourseSubCol>> getAllParticipants(String courseId) {
        // check in cache first
        if (cache.containsKey(courseId)) {
            return cache.get(courseId);
        }
        // if not in cache, query in database
        else {
            IStateLiveData<List<Participant_CourseSubCol>> liveData =
                    new MergeParticipantLiveData(userRepo, dao.read(courseId));

            cache.put(courseId, liveData);
            return liveData;
        }
    }

    public static class MergeParticipantLiveData extends StateMediatorLiveData<List<Participant_CourseSubCol>> {

        public MergeParticipantLiveData(@NonNull FirebaseUserRepository userRepo,
                                        @NonNull StateLiveData<List<Participant_CourseSubCol>> participant) {
            super(new StateModel<>(StateStatus.LOADING));

            addSource(participant, new Observer<StateModel<List<Participant_CourseSubCol>>>() {
                @Override
                public void onChanged(StateModel<List<Participant_CourseSubCol>> stateModel) {
                    switch (stateModel.getStatus()) {
                        case LOADING:
                            postLoading();
                            break;

                        case ERROR:
                            postError(stateModel.getError());
                            break;

                        case SUCCESS:
                            List<Participant_CourseSubCol> participants = stateModel.getData();
                            postSuccess(participants);

                            participants.forEach(participant -> {
                                String id = participant.getId();

                                addSource(userRepo.search(id), new Observer<StateModel<User>>() {
                                    @Override
                                    public void onChanged(StateModel<User> stateModel) {
                                        switch (stateModel.getStatus()) {
                                            case ERROR:
                                                Exception e = stateModel.getError();

                                                if (e instanceof DocumentNotFoundException) {
                                                    participant.setActive(false);
                                                    participant.setAvatar(null);
                                                    postSuccess(participants);
                                                } else {
                                                    postError(stateModel.getError());
                                                }

                                                break;

                                            case LOADING:
                                                postLoading();
                                                break;

                                            case SUCCESS:
                                                User user = stateModel.getData();
                                                participant.setActive(true);
                                                participant.setAvatar(user.getAvatar());
                                                postSuccess(participants);
                                        }
                                    }
                                });
                            });
                    }
                }
            });
        }
    }

}
