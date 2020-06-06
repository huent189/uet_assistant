package vnu.uet.mobilecourse.assistant.model.material;

import androidx.room.Entity;
import androidx.room.Ignore;
import com.google.gson.annotations.SerializedName;

import java.util.List;

//@Entity(indices = {@Index("materialId"), @Index("courseId")})
@Entity(inheritSuperIndices = true)
public class InternalResourceContent extends MaterialContent {
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
    @SerializedName("revision")
    private int revision;
//    @SerializedName("timemodified")
//    private long timeModified;
    @SerializedName("contentfiles")
//    @Relation(entityColumn = "parentId", parentColumn = "id")
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
}
