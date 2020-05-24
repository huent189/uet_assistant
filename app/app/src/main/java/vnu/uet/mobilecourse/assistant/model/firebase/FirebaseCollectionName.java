package vnu.uet.mobilecourse.assistant.model.firebase;

public interface FirebaseCollectionName {
    String USER = "user";
    String TODO_LIST = "todoList";
    String TODO = "todo";
    String COURSE = "course";
    String GROUP_CHAT = "groupChat";
    String NOTIFICATION = "notification";
    String PARTICIPANT = "participant";
    String MEMBER = "member";
    String MESSAGE = "message";
    String USER_INFO = "userInfo";

    @Deprecated
    String ENROLLMENT = "enrollment";
}