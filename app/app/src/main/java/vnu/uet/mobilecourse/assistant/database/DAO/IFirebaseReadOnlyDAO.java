package vnu.uet.mobilecourse.assistant.database.DAO;

import java.util.List;
import java.util.Map;

import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;

public interface IFirebaseReadOnlyDAO<T> {
    StateLiveData<T> read(String id);
}
