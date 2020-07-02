package vnu.uet.mobilecourse.assistant.database.DAO;

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
    String INTERESTED_DISCUSSION = "interestedDiscussion";
    String CONNECTION = "connection";
    String ONLINE_STATUS = "online";
    String TOKEN = "token";

    @Deprecated
    String ENROLLMENT = "enrollment";
}