package vnu.uet.mobilecourse.assistant.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import vnu.uet.mobilecourse.assistant.model.event.IEvent;
import vnu.uet.mobilecourse.assistant.util.FbAndCourseMap;

import java.util.Date;
@Entity()
public class FinalExam implements IEvent, Parcelable {
    @PrimaryKey
    @NonNull
    private String classCode;
    private String className;
    private long examTime;
    private String place;
    private String form;
    private String IdNumber;

    public FinalExam() {

    }

    protected FinalExam(Parcel in) {
        classCode = in.readString();
        className = in.readString();
        examTime = in.readLong();
        place = in.readString();
        form = in.readString();
        IdNumber = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(classCode);
        dest.writeString(className);
        dest.writeLong(examTime);
        dest.writeString(place);
        dest.writeString(form);
        dest.writeString(IdNumber);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FinalExam> CREATOR = new Creator<FinalExam>() {
        @Override
        public FinalExam createFromParcel(Parcel in) {
            return new FinalExam(in);
        }

        @Override
        public FinalExam[] newArray(int size) {
            return new FinalExam[size];
        }
    };

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public long getExamTime() {
        return examTime;
    }

    public void setExamTime(Date examTime) {
        this.examTime = examTime.getTime();
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getIdNumber() {
        return IdNumber;
    }

    public void setIdNumber(String idNumber) {
        IdNumber = idNumber;
    }

    public void setExamTime(long examTime) {
        this.examTime = examTime;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof  FinalExam)){
            return false;
        }
        FinalExam exam = (FinalExam) obj;
        return (classCode.equals(exam.classCode) && examTime == exam.examTime
                && place.equals(exam.place) && IdNumber.equals(exam.IdNumber));
    }

    @Override
    public String getTitle() {
        return "Thi cuối kỳ: " + className;
    }

    @Override
    public String getCategory() {
        return FbAndCourseMap.cleanCode(classCode);
    }

    @Override
    public Date getTime() {
        return new Date(examTime);
    }

    @Override
    public boolean isCompleted() {
        return examTime < System.currentTimeMillis();
    }
}
