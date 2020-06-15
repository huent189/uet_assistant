package vnu.uet.mobilecourse.assistant.model.firebase;

import android.os.Parcel;
import android.os.Parcelable;

import vnu.uet.mobilecourse.assistant.util.StringConst;

public class CourseSession implements Parcelable {
    /**
     * Type of course session
     * 0 - CL
     * i > 0 - Ni
     */
    private int type;

    private String courseName;
    private String courseCode;

    private String classroom;

    private String teacherName;

    private int dayOfWeek;
    private int start, end;

    public CourseSession() {

    }

    protected CourseSession(Parcel in) {
        type = in.readInt();
        courseName = in.readString();
        courseCode = in.readString();
        classroom = in.readString();
        teacherName = in.readString();
        dayOfWeek = in.readInt();
        start = in.readInt();
        end = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(courseName);
        dest.writeString(courseCode);
        dest.writeString(classroom);
        dest.writeString(teacherName);
        dest.writeInt(dayOfWeek);
        dest.writeInt(start);
        dest.writeInt(end);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CourseSession> CREATOR = new Creator<CourseSession>() {
        @Override
        public CourseSession createFromParcel(Parcel in) {
            return new CourseSession(in);
        }

        @Override
        public CourseSession[] newArray(int size) {
            return new CourseSession[size];
        }
    };

    public String getId() {
        return courseCode + StringConst.UNDERSCORE_CHAR + type;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public final int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "CourseSession{" +
                "type=" + type +
                ", courseName='" + courseName + '\'' +
                ", classroom='" + classroom + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", dayOfWeek=" + dayOfWeek +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
