package vnu.uet.mobilecourse.assistant.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

@Entity
public class Course {
    @PrimaryKey
    @NonNull
    private int id;

    @SerializedName("displayname")
    private String title;

    @SerializedName("idnumber")
    private String code;

    @Ignore
    private Date lastAccessTime;

    @Ignore
    private int thumbnail;

    public Course(String title, String code) {
        this.title = title;
        this.code = code;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

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

    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(Date accessTime) {
        this.lastAccessTime = accessTime;
    }

    @Override
    public String toString() {
        return "Course{" +
                "title='" + title + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
