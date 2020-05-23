package vnu.uet.mobilecourse.assistant.model.firebase;

import com.google.firebase.firestore.Exclude;

public class CourseSession {
    /**
     * Type of course session
     * 0 - CL
     * i > 0 - Ni
     */
    private int type;

    private String classroom;

    private String teacherName;

    @Exclude
    private String time;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    @Exclude
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
