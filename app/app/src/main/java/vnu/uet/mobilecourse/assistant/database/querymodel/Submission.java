package vnu.uet.mobilecourse.assistant.database.querymodel;

import vnu.uet.mobilecourse.assistant.model.event.CourseSubmissionEvent;

public class Submission {
    private  int courseId;
    private String courseName;
    private int materialId;
    private String materialName;
    private String type;
    private long startTime;
    private long endTime;
    private boolean isCompleted;

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public CourseSubmissionEvent toStartEvent(){
        return new CourseSubmissionEvent(courseId, materialId, courseName,
                materialName + ": bắt đầu diễn ra", startTime, isCompleted, type);
    }
    public CourseSubmissionEvent toEndEvent(){
        return new CourseSubmissionEvent(courseId, materialId, courseName,
                materialName + ": kết thúc", endTime, isCompleted, type);
    }
}
