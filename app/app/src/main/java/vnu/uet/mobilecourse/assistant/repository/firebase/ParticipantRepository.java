package vnu.uet.mobilecourse.assistant.repository.firebase;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import vnu.uet.mobilecourse.assistant.database.DAO.ParticipantDAO;
import vnu.uet.mobilecourse.assistant.exception.DocumentNotFoundException;
import vnu.uet.mobilecourse.assistant.model.firebase.Participant_CourseSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.User;
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

        private List<String> addedStudents = new ArrayList<>();

        public MergeParticipantLiveData(@NonNull FirebaseUserRepository userRepo,
                                        @NonNull StateLiveData<List<Participant_CourseSubCol>> participant) {
            super(new StateModel<>(StateStatus.LOADING));

            addSource(participant, participantState -> {
                switch (participantState.getStatus()) {
                    case LOADING:
                        postLoading();
                        break;

                    case ERROR:
                        postError(participantState.getError());
                        break;

                    case SUCCESS:
                        List<Participant_CourseSubCol> participants = participantState.getData();
                        postSuccess(participants);

                        participants.forEach(participant1 -> {
                            String id = participant1.getId();

                            if (!addedStudents.contains(id)) {
                                addedStudents.add(id);

                                addSource(userRepo.search(id), profileState -> {
                                    switch (profileState.getStatus()) {
                                        case ERROR:
                                            Exception e = profileState.getError();

                                            if (e instanceof DocumentNotFoundException) {
                                                participant1.setActive(false);
                                                participant1.setAvatar(null);
                                                postSuccess(participants);
                                            } else {
                                                postError(profileState.getError());
                                            }

                                            break;

                                        case LOADING:
                                            postLoading();
                                            break;

                                        case SUCCESS:
                                            User user = profileState.getData();
                                            participant1.setActive(true);
                                            participant1.setAvatar(user.getAvatar());
                                            postSuccess(participants);
                                    }
                                });
                            }
                        });
                }
            });
        }
    }

}
