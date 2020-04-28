package vnu.uet.mobilecourse.assistant.viewmodel.state;

public class StateModel<T> {
    private StateStatus status;
    private T data;
    private Exception error;
    public StateStatus getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public Exception getError() {
        return error;
    }

    public StateModel(StateStatus status, Exception error) {
        this.status = status;
        this.error = error;
    }

    public StateModel(StateStatus status, T data) {
        this.status = status;
        this.data = data;
    }

    public StateModel(StateStatus status) {
        this.status = status;
    }
}
