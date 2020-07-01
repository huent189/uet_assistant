package vnu.uet.mobilecourse.assistant.model.notification;

public interface NotificationType {
    /**
     * deadline is coming...
     */
    int TODO = 1;

    /**
     * new material
     */
    int MATERIAL = 2;

    /**
     * go to school now!!
     */
    int ATTENDANCE = 3;

    /**
     * admin notification
     */
    int ADMIN = 4;

    /**
     * new forum post;
     */
    int FORUM = 5;

    /**
     * submission deadline
     */
    int SUBMISSION = 6;

    /**
     *  final exam schedule changed
     */
    int FINAL_EXAM = 7;
}
