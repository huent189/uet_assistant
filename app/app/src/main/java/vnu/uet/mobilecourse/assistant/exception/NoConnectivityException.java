package vnu.uet.mobilecourse.assistant.exception;

public class NoConnectivityException extends HostIsNotReachable {
    public NoConnectivityException() {
        super("Internet is unavailable");
    }
}
