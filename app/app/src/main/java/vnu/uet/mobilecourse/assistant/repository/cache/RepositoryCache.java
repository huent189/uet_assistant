package vnu.uet.mobilecourse.assistant.repository.cache;

import java.util.HashMap;

import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public abstract class RepositoryCache<T> extends HashMap<String, IStateLiveData<T>> {

}
