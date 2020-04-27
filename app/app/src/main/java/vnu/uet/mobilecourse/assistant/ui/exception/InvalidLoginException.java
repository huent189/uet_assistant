package vnu.uet.mobilecourse.assistant.ui.exception;

public class InvalidLoginException extends Exception {
    public InvalidLoginException() {
        super("Wrong username or password");
    }

    public InvalidLoginException(String message) {
        super(message);
    }
}
