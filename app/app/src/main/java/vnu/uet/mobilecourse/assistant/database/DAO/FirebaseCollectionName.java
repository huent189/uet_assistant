package vnu.uet.mobilecourse.assistant.database.DAO;

public interface FirebaseCollectionName {
    String USER = "user";
    String TODO_LIST = "todoList";
    String TODO = "todo";
    String COURSE = "course";
    String GROUP_CHAT = "groupChat";
//    String GROUP_CHAT_SUB_COL = "groupChat_subCol";
    String NOTIFICATION = "notification";
    String PARTICIPANT = "participant";
    String MEMBER = "member";
    String MESSAGE = "message";
    String USER_INFO = "userInfo";

    @Deprecated
    String ENROLLMENT = "enrollment";
}