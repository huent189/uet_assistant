package vnu.uet.mobilecourse.assistant.repository.firebase;

import java.util.List;

import vnu.uet.mobilecourse.assistant.database.DAO.ParticipantDAO;
import vnu.uet.mobilecourse.assistant.model.firebase.Participant_CourseSubCol;
import vnu.uet.mobilecourse.assistant.repository.cache.ParticipantCache;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public class ParticipantRepository {

    private static ParticipantRepository instance;

    private ParticipantCache cache;

    private ParticipantDAO dao;

    public static ParticipantRepository getInstance() {
        if (instance == null) {
            instance = new ParticipantRepository();
        }

        return instance;
    }

    public ParticipantRepository() {
        cache = new ParticipantCache();
        dao = new ParticipantDAO();
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
            IStateLiveData<List<Participant_CourseSubCol>> liveData = dao.read(courseId);
            cache.put(courseId, liveData);
            return liveData;
        }
    }
}
