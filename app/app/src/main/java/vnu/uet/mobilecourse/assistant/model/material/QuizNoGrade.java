package vnu.uet.mobilecourse.assistant.model.material;

import androidx.room.Entity;
import com.google.gson.annotations.SerializedName;
@Entity(inheritSuperIndices = true)
public class QuizNoGrade extends MaterialContent {
    @SerializedName("timeopen")
    private long timeOpen;
    @SerializedName("timeclose")
    private long timeClose;
    @SerializedName("timelimit")
    private long timeLimit;
    @SerializedName("attempts")
    private int maximumAttemp;
    @SerializedName("grade")
    private int maximumGrade;
    //TODO: view grade field

    public long getTimeOpen() {
        return timeOpen;
    }

    public void setTimeOpen(long timeOpen) {
        this.timeOpen = timeOpen;
    }

    public long getTimeClose() {
        return timeClose;
    }

    public void setTimeClose(long timeClose) {
        this.timeClose = timeClose;
    }

    public long getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getMaximumAttemp() {
        return maximumAttemp;
    }

    public void setMaximumAttemp(int maximumAttemp) {
        this.maximumAttemp = maximumAttemp;
    }

    public int getMaximumGrade() {
        return maximumGrade;
    }

    public void setMaximumGrade(int maximumGrade) {
        this.maximumGrade = maximumGrade;
    }

    @Override
    public String toString() {
        return "QuizNoGrade{" +
                "timeOpen=" + timeOpen +
                ", timeClose=" + timeClose +
                ", timeLimit=" + timeLimit +
                ", maximumAttemp=" + maximumAttemp +
                ", maximumGrade=" + maximumGrade +
                '}';
    }
}
