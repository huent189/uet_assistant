package vnu.uet.mobilecourse.assistant.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity()
public class Assignment {
    @PrimaryKey
    private int id;
    private int courseId;
    private double userGrade;
    private double maxGrade;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public double getUserGrade() {
        return userGrade;
    }

    public void setUserGrade(double userGrade) {
        this.userGrade = userGrade;
    }

    public double getMaxGrade() {
        return maxGrade;
    }

    public void setMaxGrade(double maxGrade) {
        this.maxGrade = maxGrade;
    }
}
