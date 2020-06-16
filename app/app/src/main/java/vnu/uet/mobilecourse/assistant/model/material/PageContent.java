package vnu.uet.mobilecourse.assistant.model.material;

import androidx.room.Entity;
import com.google.gson.annotations.SerializedName;
@Entity(inheritSuperIndices = true)
public class PageContent extends MaterialContent {
    @SerializedName("revision")
    private int revision;
    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }
    public String getType(){
        return CourseConstant.MaterialType.PAGE;
    }
    @Override
    public String toString() {
        return "PageContent{" +
                "revision=" + revision +
                '}';
    }
}
