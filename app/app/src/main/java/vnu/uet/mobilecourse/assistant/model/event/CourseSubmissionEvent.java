package vnu.uet.mobilecourse.assistant.model.event;

import java.util.Date;

public class CourseSubmissionEvent implements IEvent {
    private int courseId;
    private int materialId;
    private String courseName;
    private String title;
    private long time;
    private boolean isCompleted;
    private String type;
    public String getType() {
        return type;
    }

    public CourseSubmissionEvent(int courseId, int materialId, String courseName, String title, long time, boolean isCompleted, String type) {
        this.courseId = courseId;
        this.materialId = materialId;
        this.courseName = courseName;
        this.title = title;
        this.time = time;
        this.isCompleted = isCompleted;
        this.type = type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getCategory() {
        return courseName;
    }

    @Override
    public Date getTime() {
        return new Date(time);
    }

    @Override
    public boolean isCompleted() {
        return isCompleted;
    }
}
