package vnu.uet.mobilecourse.assistant.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
@Entity()
public class FinalExam {
    @PrimaryKey
    @NonNull
    private String classCode;
    private String className;
    private long examTime;
    private String place;
    private String form;
    private String IdNumber;

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

}
