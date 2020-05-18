package vnu.uet.mobilecourse.assistant.viewmodel.state;

import androidx.lifecycle.LiveData;

public class StateLiveData<T> extends AbstractStateLiveData<T> {
    public StateLiveData() {
    }

    public StateLiveData(StateModel<T> value) {
        super(value);
    }
}
