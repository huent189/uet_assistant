package vnu.uet.mobilecourse.assistant.model.firebase;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserInfo {
    private String stuentId;
    private String name;
    private long DOB;
    private String uetClass;

    public String getStuentId() {
        return stuentId;
    }

    public void setStuentId(String stuentId) {
        this.stuentId = stuentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDOB() {
        return DOB;
    }

    public void setDOB(long DOB) {
        this.DOB = DOB;
    }

    public String getUetClass() {
        return uetClass;
    }

    public void setUetClass(String uetClass) {
        this.uetClass = uetClass;
    }
}
