package vnu.uet.mobilecourse.assistant.exception;

public class OverrideDocumentException extends FirebaseException {

    public OverrideDocumentException(String message) {
        super(message);
    }

    public OverrideDocumentException() {
        super("You try to override an exist document");
    }
}
