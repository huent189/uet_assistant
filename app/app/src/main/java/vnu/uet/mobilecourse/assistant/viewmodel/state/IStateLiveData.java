package vnu.uet.mobilecourse.assistant.viewmodel.state;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

public interface IStateLiveData<T> {
    void postLoading();

    void postSuccess(@NonNull T data);

    void postError(@NonNull Exception e);

    void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super StateModel<T>> observer);

    void observeForever(@NonNull Observer<? super StateModel<T>> observer);

    void removeObserver(@NonNull final Observer<? super StateModel<T>> observer);

    StateModel<T> getValue();
}
