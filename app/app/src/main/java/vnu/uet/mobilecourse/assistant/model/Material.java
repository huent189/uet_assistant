package vnu.uet.mobilecourse.assistant.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = WeeklyMaterial.class, parentColumns = "id",
        childColumns = "weekId", onDelete = CASCADE),
        indices = {@Index("weekId")})
public class Material implements Parcelable {
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
    @SerializedName("instance")
    private int instanceId;
    private int completion;
    private String fileName;
    private String fileUrl;
    private long lastModified;

    public int getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    public Material() {

    }

    public Material(Parcel in) {
        id = in.readInt();
        weekId = in.readInt();
        title = in.readString();
        type = in.readString();
        description = in.readString();
        completion = in.readInt();
        fileName = in.readString();
        fileUrl = in.readString();
        lastModified = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(weekId);
        dest.writeString(title);
        dest.writeString(type);
        dest.writeString(description);
        dest.writeInt(completion);
        dest.writeString(fileName);
        dest.writeString(fileUrl);
        dest.writeLong(lastModified);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Material> CREATOR = new Creator<Material>() {
        @Override
        public Material createFromParcel(Parcel in) {
            return new Material(in);
        }

        @Override
        public Material[] newArray(int size) {
            return new Material[size];
        }
    };

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
        if(!type.equals("url")){
            this.fileUrl = fileUrl + "&token=" + User.getInstance().getToken();
        }
        else {
            this.fileUrl = fileUrl;
        }
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
