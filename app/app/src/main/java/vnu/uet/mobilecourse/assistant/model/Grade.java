package vnu.uet.mobilecourse.assistant.model;

import androidx.room.*;
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
    @SerializedName("itemmodule")
    private String type;
    @SerializedName("cmid")
    private int materialId;
    @SerializedName("gradedategraded")
    private long gradedDate;
    @Ignore
    @SerializedName("itemtype")
    private String parentType;

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public long getGradedDate() {
        return gradedDate;
    }

    public void setGradedDate(long gradedDate) {
        this.gradedDate = gradedDate;
    }

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

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public static final String ASSIGN = "assign";

    public static final String QUIZ = "quiz";

    @Override
    public String toString() {
        return "Grade{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", name='" + name + '\'' +
                ", userGrade=" + userGrade +
                ", maxGrade=" + maxGrade +
                ", type='" + type + '\'' +
                ", materialId=" + materialId +
                ", gradedDate=" + gradedDate +
                '}';
    }
}
