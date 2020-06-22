package vnu.uet.mobilecourse.assistant.model.event;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.util.FbAndCourseMap;

public class CourseSubmissionEvent implements IEvent, Parcelable {

    private int courseId;
    private int materialId;
    private String courseName;
    private String title;
    private long time;
    private boolean isCompleted;
    private String type;

    protected CourseSubmissionEvent(Parcel in) {
        courseId = in.readInt();
        materialId = in.readInt();
        courseName = in.readString();
        title = in.readString();
        time = in.readLong();
        isCompleted = in.readByte() != 0;
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(courseId);
        dest.writeInt(materialId);
        dest.writeString(courseName);
        dest.writeString(title);
        dest.writeLong(time);
        dest.writeByte((byte) (isCompleted ? 1 : 0));
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CourseSubmissionEvent> CREATOR = new Creator<CourseSubmissionEvent>() {
        @Override
        public CourseSubmissionEvent createFromParcel(Parcel in) {
            return new CourseSubmissionEvent(in);
        }

        @Override
        public CourseSubmissionEvent[] newArray(int size) {
            return new CourseSubmissionEvent[size];
        }
    };

    public String getType() {
        return type;
    }

    public CourseSubmissionEvent(int courseId, int materialId, String courseName, String title,
                                 long time, boolean isCompleted, String type) {
        this.courseId = courseId;
        this.materialId = materialId;
        this.courseName = courseName;
        this.title = title;
        this.time = time;
        this.isCompleted = isCompleted;
        this.type = type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getCategory() {
        return FbAndCourseMap.cleanCode(courseName);
    }

    @Override
    public Date getTime() {
        return DateTimeUtils.fromSecond(time);
    }

    @Override
    public boolean isCompleted() {
        return isCompleted;
    }
}
