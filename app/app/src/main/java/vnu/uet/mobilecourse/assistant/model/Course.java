package vnu.uet.mobilecourse.assistant.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

@Entity
public class Course implements ICourse {
    @PrimaryKey
    private int id;
    @Ignore
    private int thumbnail;
    @SerializedName("displayname")
    private String title;
    @SerializedName("idnumber")
    private String code;
    @SerializedName("lastaccess")
    private long lastAccessTime;
    @SerializedName("progress")
    private double progress;
    public Course(String title, String code) {
        this.title = title;
        this.code = code;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirebaseId(){
        return code.replace("1920II_" , "").replace("_", " ");
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", code='" + code + '\'' +
                ", lastAccessTime=" + lastAccessTime +
                '}';
    }
}
