package vnu.uet.mobilecourse.assistant.exception;

public class UnavailableHostException extends HostIsNotReachable {
    public UnavailableHostException() {
        super("server is down");
    }

    public UnavailableHostException(String message) {
        super(message);
    }
}
