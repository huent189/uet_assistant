package vnu.uet.mobilecourse.assistant.model.material;

import androidx.room.Entity;
import androidx.room.Ignore;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(inheritSuperIndices = true)
public class InternalResourceContent extends MaterialContent {
    @SerializedName("revision")
    private int revision;
    @SerializedName("contentfiles")
    @Ignore
    private List<InternalFile> files;

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public List<InternalFile> getFiles() {
        return files;
    }

    public void setFiles(List<InternalFile> files) {
        this.files = files;
    }
    public String getType(){
        return CourseConstant.MaterialType.RESOURCE;
    }
    @Override
    public String toString() {
        return super.toString() + "InternalResourceContent{" +
                "revision=" + revision +
                ", files=" + files +
                '}';
    }
}
