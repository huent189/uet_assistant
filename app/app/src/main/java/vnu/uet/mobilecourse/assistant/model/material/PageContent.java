package vnu.uet.mobilecourse.assistant.model.material;

import androidx.room.Entity;
import com.google.gson.annotations.SerializedName;
//@Entity(indices = {@Index("materialId"), @Index("courseId")})
@Entity(inheritSuperIndices = true)
public class PageContent extends MaterialContent {
//    @PrimaryKey
//    @SerializedName("id")
//    private int id;
//    @SerializedName("coursemodule")
//    private int materialId;
//    @SerializedName("course")
//    private int courseId;
//    @SerializedName("name")
//    private String name;
//    //merge content vs intro
//    private String intro;
    @SerializedName("revision")
    private int revision;
//    @SerializedName("timemodified")
//    private long timeModified;

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }
}
