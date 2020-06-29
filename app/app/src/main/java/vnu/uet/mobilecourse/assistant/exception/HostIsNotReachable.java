package vnu.uet.mobilecourse.assistant.exception;

import java.io.IOException;

public class HostIsNotReachable extends IOException {
    public HostIsNotReachable() {
    }

    public HostIsNotReachable(String message) {
        super(message);
    }
}
