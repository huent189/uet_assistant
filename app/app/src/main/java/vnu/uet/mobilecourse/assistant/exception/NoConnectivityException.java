package vnu.uet.mobilecourse.assistant.exception;

import java.io.IOException;

public class NoConnectivityException extends IOException {
    public NoConnectivityException() {
        super("Internet is unavailable");
    }
}
