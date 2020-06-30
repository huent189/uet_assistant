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

    private ParticipantCache mCache;
    private ParticipantDAO mDao;
    private FirebaseUserRepository mUserRepo;

    public static ParticipantRepository getInstance() {
        if (instance == null) {
            instance = new ParticipantRepository();
        }

        return instance;
    }

    private ParticipantRepository() {
        mCache = new ParticipantCache();
        mDao = new ParticipantDAO();
        mUserRepo = FirebaseUserRepository.getInstance();
    }

    /**
     * Get all participants in a specific course
     *
     * @param courseId of course
     * @return state live data contains result
     */
    public IStateLiveData<List<Participant_CourseSubCol>> getAllParticipants(String courseId) {
        // check in cache first
        if (mCache.containsKey(courseId)) {
            return mCache.get(courseId);
        }
        // if not in cache, query in database
        else {
            IStateLiveData<List<Participant_CourseSubCol>> liveData =
                    new MergeParticipantLiveData(mUserRepo, mDao.read(courseId));

            mCache.put(courseId, liveData);
            return liveData;
        }
    }

    public static class MergeParticipantLiveData extends StateMediatorLiveData<List<Participant_CourseSubCol>> {

        private static final int MINIMUM_PARTICIPANTS = 20;

        private List<String> mAddedStudents = new ArrayList<>();

        MergeParticipantLiveData(@NonNull FirebaseUserRepository userRepo,
                                 @NonNull StateLiveData<List<Participant_CourseSubCol>> participantsLiveData) {
            super(new StateModel<>(StateStatus.LOADING));

            addSource(participantsLiveData, participantState -> {
                switch (participantState.getStatus()) {
                    case LOADING:
                        postLoading();
                        break;

                    case ERROR:
                        postError(participantState.getError());
                        break;

                    case SUCCESS:
                        List<Participant_CourseSubCol> participants = participantState.getData();

                        if (participants.size() >= MINIMUM_PARTICIPANTS) {
                            postSuccess(participants);

                            participants.forEach(participant -> {
                                String id = participant.getId();

                                if (!mAddedStudents.contains(id)) {
                                    mAddedStudents.add(id);

                                    addSource(userRepo.search(id), profileState -> {
                                        switch (profileState.getStatus()) {
                                            case ERROR:
                                                Exception e = profileState.getError();

                                                if (e instanceof DocumentNotFoundException) {
                                                    participant.setActive(false);
                                                    participant.setAvatar(null);
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
                                                participant.setActive(true);
//                                                participant.setAvatar(user.getAvatar());
                                                postSuccess(participants);
                                        }
                                    });
                                }
                            });
                        }
                }
            });
        }
    }

}
