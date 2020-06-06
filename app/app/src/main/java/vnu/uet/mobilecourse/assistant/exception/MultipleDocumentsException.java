package vnu.uet.mobilecourse.assistant.exception;

public class MultipleDocumentsException extends FirebaseException {

    public MultipleDocumentsException(String id) {
        super("Found multiple documents with the given id: " + id);
    }
}
