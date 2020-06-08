package vnu.uet.mobilecourse.assistant.database.DAO;

import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;

public interface IFirebaseReadOnlyDAO<T> {
    StateLiveData<T> read(String id);
}
