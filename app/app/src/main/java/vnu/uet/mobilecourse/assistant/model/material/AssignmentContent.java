package vnu.uet.mobilecourse.assistant.model.material;

import androidx.room.Entity;
import com.google.gson.annotations.SerializedName;
//@Entity(indices = {@Index("materialId"), @Index("courseId")})
@Entity(inheritSuperIndices = true)
public class AssignmentContent extends MaterialContent {
//    @PrimaryKey
//    @SerializedName("id")
//    private int id;
//    @SerializedName("coursemodule")
//    private int materialId;
//    @SerializedName("course")
//    private int courseId;
//    @SerializedName("name")
//    private String name;
//    @SerializedName("intro")
//    private String intro;
    @SerializedName("duedate")
    private long deadline;
    @SerializedName("allowsubmissionsfromdate")
    private long startDate;
    @SerializedName("grade")
    private int maximumGrade;
    @SerializedName("completionsubmit")
    private boolean isCompleted;
    @SerializedName("maxattempts")
    private int maxAttemptAllowed;
    //TODO: submission field

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public int getMaximumGrade() {
        return maximumGrade;
    }

    public void setMaximumGrade(int maximumGrade) {
        this.maximumGrade = maximumGrade;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public int getMaxAttemptAllowed() {
        return maxAttemptAllowed;
    }

    public void setMaxAttemptAllowed(int maxAttemptAllowed) {
        this.maxAttemptAllowed = maxAttemptAllowed;
    }
}
