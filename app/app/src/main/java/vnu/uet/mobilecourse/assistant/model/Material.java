package vnu.uet.mobilecourse.assistant.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = WeeklyMaterial.class, parentColumns = "id",
        childColumns = "weekId", onDelete = CASCADE),
        indices = {@Index("weekId")})
public class Material {
    @PrimaryKey
    @SerializedName("id")
    private int id;
    private int weekId;
    @SerializedName("name")
    private String title;
    @SerializedName("modname")
    private String type;
    @SerializedName("description")
    private String description;
    @SerializedName("completion")
    private int completion;
    private String fileName;
    private String fileUrl;
    private long lastModified;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWeekId() {
        return weekId;
    }

    public void setWeekId(int weekId) {
        this.weekId = weekId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCompletion() {
        return completion;
    }

    public void setCompletion(int completion) {
        this.completion = completion;
    }

    @Override
    public String toString() {
        return "Material{" +
                "id=" + id +
                ", weekId=" + weekId +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", completion=" + completion +
                ", fileName='" + fileName + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", lastModified=" + lastModified +
                '}';
    }
}
