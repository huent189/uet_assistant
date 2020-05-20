package vnu.uet.mobilecourse.assistant.viewmodel.state;

public class StateModel<T> {

    private StateStatus mStatus;
    private T mData;
    private Exception mError;

    public StateStatus getStatus() {
        return mStatus;
    }

    public T getData() {
        return mData;
    }

    public Exception getError() {
        return mError;
    }

    public StateModel(StateStatus status, Exception error) {
        this.mStatus = status;
        this.mError = error;
    }

    public StateModel(StateStatus status, T data) {
        this.mStatus = status;
        this.mData = data;
    }

    public StateModel(StateStatus status) {
        this.mStatus = status;
    }
}
