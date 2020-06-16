package vnu.uet.mobilecourse.assistant.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import vnu.uet.mobilecourse.assistant.R;

import com.google.gson.annotations.SerializedName;

import java.util.Random;

@Entity
public class Course implements ICourse {
    @PrimaryKey
    private int id;
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

    protected Course(Parcel in) {
        id = in.readInt();
        title = in.readString();
        code = in.readString();
        lastAccessTime = in.readLong();
        progress = in.readDouble();
    }

    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public int getThumbnail() {
        int random = id % 3;

        if (random == 2) {
            return R.drawable.isometric_course_thumbnail;
        } else if (random == 1) {
            return R.drawable.isomatric_idea_course;
        } else {
            return R.drawable.isomatric_course_online;
        }
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(code);
        dest.writeLong(lastAccessTime);
        dest.writeDouble(progress);
    }
}
