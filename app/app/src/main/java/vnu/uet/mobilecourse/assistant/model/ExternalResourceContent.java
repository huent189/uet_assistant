package vnu.uet.mobilecourse.assistant.model;

import androidx.room.Entity;
import com.google.gson.annotations.SerializedName;
//@Entity(indices = {@Index("materialId"), @Index("courseId")})
@Entity(inheritSuperIndices = true)
public class ExternalResourceContent extends MaterialContent {
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
    @SerializedName("externalurl")
    private String fileUrl;
    @SerializedName("revision")
    private int revision;
//    @SerializedName("timemodified")
//    private long timeModified;

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
}
