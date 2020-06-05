package vnu.uet.mobilecourse.assistant.exception;

public class DocumentNotFoundException extends FirebaseException {

    public DocumentNotFoundException(String id) {
        super("Can not found any document with the given id: " + id);
    }
}
