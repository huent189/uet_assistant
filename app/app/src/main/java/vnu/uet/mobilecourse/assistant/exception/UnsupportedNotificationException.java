package vnu.uet.mobilecourse.assistant.exception;

public class UnsupportedNotificationException extends Exception {
    public UnsupportedNotificationException(int type) {
        super("This type of notification haven't supported yet - type id " + type);
    }
}
