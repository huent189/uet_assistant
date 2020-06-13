package vnu.uet.mobilecourse.assistant.model.material;

import androidx.room.Entity;
import com.google.gson.annotations.SerializedName;
@Entity(inheritSuperIndices = true)
public class AssignmentContent extends MaterialContent {
    @SerializedName("duedate")
    private long deadline;
    @SerializedName("allowsubmissionsfromdate")
    private long startDate;
    @SerializedName("grade")
    private int maximumGrade;
    @SerializedName("completionsubmit")
    private int isCompleted;
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

    public int getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(int isCompleted) {
        this.isCompleted = isCompleted;
    }

    public int getMaxAttemptAllowed() {
        return maxAttemptAllowed;
    }

    public void setMaxAttemptAllowed(int maxAttemptAllowed) {
        this.maxAttemptAllowed = maxAttemptAllowed;
    }
    public String getType(){
        return CourseConstant.MaterialType.ASSIGN;
    }
    @Override
    public String toString() {
        return "AssignmentContent{" +
                "deadline=" + deadline +
                ", startDate=" + startDate +
                ", maximumGrade=" + maximumGrade +
                ", isCompleted=" + isCompleted +
                ", maxAttemptAllowed=" + maxAttemptAllowed +
                '}';
    }
}
