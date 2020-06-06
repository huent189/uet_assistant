package vnu.uet.mobilecourse.assistant.exception;

import java.io.IOException;

public class UnavailableHostException extends IOException {
    public UnavailableHostException() {
        super("server is down");
    }
}
