package vnu.uet.mobilecourse.assistant.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Course.class, parentColumns = "id",
                                childColumns = "courseId", onDelete = CASCADE),
        indices = {@Index("courseId")})
public class WeeklyMaterial {
    @PrimaryKey
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String title;
    private int courseId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    @Override
    public String toString() {
        return "WeeklyMaterial{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", courseId=" + courseId +
                '}';
    }
}
