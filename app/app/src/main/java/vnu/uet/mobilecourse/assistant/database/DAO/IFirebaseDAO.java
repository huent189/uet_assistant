package vnu.uet.mobilecourse.assistant.database.DAO;

import java.util.List;
import java.util.Map;

import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;

public interface IFirebaseDAO<T> {

    /**
     * Get all data in firestore db
     * and update live data whenever a snapshot change
     */
    StateLiveData<List<T>> readAll();

    StateMediatorLiveData<T> read(String id);

    StateLiveData<T> add(String id, T document);

    StateLiveData<String> delete(String id);

    StateLiveData<String> update(String id, Map<String, Object> changes);
}
