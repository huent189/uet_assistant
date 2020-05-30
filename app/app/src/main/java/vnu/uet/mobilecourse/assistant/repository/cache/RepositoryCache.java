package vnu.uet.mobilecourse.assistant.repository.cache;

import java.util.HashMap;

import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

/**
 * Cache map from id to live data contains value
 *
 * @param <T> value type
 */
public abstract class RepositoryCache<T> extends HashMap<Object, IStateLiveData<T>> {

}
