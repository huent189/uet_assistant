package vnu.uet.mobilecourse.assistant.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Course.class, parentColumns = "id",
        childColumns = "courseId", onDelete = CASCADE),
        indices = {@Index("courseId")})
public class Grade {
    @PrimaryKey
    @SerializedName("id")
    private int id;
    private int courseId;
    @SerializedName("itemname")
    private String name;
    @SerializedName("graderaw")
    private double userGrade;
    @SerializedName("grademax")
    private double maxGrade;
    @SerializedName("itemtype")
    private String type;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", name='" + name + '\'' +
                ", userGrade=" + userGrade +
                ", maxGrade=" + maxGrade +
                ", type='" + type + '\'' +
                '}';
    }
}
