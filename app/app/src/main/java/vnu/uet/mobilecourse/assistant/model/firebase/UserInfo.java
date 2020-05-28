package vnu.uet.mobilecourse.assistant.model.firebase;


import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

public class UserInfo implements IFirebaseModel {

    @PropertyName("stuentId")
    private String id;

    private String name;

    @PropertyName("dob")
    private long DOB;

    private String uetClass;

    @Exclude
    private String avatar;

    @Override
    @PropertyName("stuentId")
    public String getId() {
        return id;
    }

    @PropertyName("stuentId")
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @PropertyName("dob")
    public long getDOB() {
        return DOB;
    }

    @PropertyName("dob")
    public void setDOB(long DOB) {
        this.DOB = DOB;
    }

    public String getUetClass() {
        return uetClass;
    }

    public void setUetClass(String uetClass) {
        this.uetClass = uetClass;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
