package vnu.uet.mobilecourse.assistant.model.notification;

public class FinalExamNotification extends Notification_UserSubCol {

    private String courseCode;

    public FinalExamNotification() {
        super(NotificationType.FINAL_EXAM);
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
}
