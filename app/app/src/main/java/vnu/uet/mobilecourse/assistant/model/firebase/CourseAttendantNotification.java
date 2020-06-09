package vnu.uet.mobilecourse.assistant.model.firebase;

import com.google.firebase.firestore.Exclude;

public class CourseAttendantNotification extends Notification_UserSubCol {

    private String courseCode;

    @Exclude
    private CourseInfo course;

    public CourseAttendantNotification() {
        super(NotificationType.ATTENDANCE);
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    @Exclude
    public CourseInfo getCourse() {
        return course;
    }

    public void setCourse(CourseInfo course) {
        this.course = course;
    }
}
