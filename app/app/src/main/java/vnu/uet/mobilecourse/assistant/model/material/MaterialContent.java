package vnu.uet.mobilecourse.assistant.model.material;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;
@Entity(indices = {@Index("materialId"), @Index("courseId")})
public class MaterialContent {
    @PrimaryKey
    @SerializedName("id")
    private int id;
    @SerializedName(value = "coursemodule", alternate = {"cmid"})
    private int materialId;
    @SerializedName("course")
    private int courseId;
    @SerializedName("name")
    private String name;
    @SerializedName("intro")
    private String intro;
    @SerializedName("timemodified")
    private long timeModified;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public long getTimeModified() {
        return timeModified;
    }

    public void setTimeModified(long timeModified) {
        this.timeModified = timeModified;
    }
    public String getType(){
        return CourseConstant.MaterialType.LABEL;
    }
    @Override
    public String toString() {
        return "MaterialContent{" +
                "id=" + id +
                ", materialId=" + materialId +
                ", courseId=" + courseId +
                ", name='" + name + '\'' +
                ", intro='" + intro + '\'' +
                ", timeModified=" + timeModified +
                '}';
    }
}
