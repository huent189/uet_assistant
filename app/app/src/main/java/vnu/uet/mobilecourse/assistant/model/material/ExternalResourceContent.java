package vnu.uet.mobilecourse.assistant.model.material;

import androidx.room.Entity;
import com.google.gson.annotations.SerializedName;
//@Entity(indices = {@Index("materialId"), @Index("courseId")})
@Entity(inheritSuperIndices = true)
public class ExternalResourceContent extends MaterialContent {
    @SerializedName("externalurl")
    private String fileUrl;
    @SerializedName("revision")
    private int revision;
    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }
    public String getType(){
        return CourseConstant.MaterialType.URL;
    }
    @Override
    public String toString() {
        return "ExternalResourceContent{" +
                "fileUrl='" + fileUrl + '\'' +
                ", revision=" + revision +
                '}';
    }
}
