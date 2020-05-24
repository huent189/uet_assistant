package vnu.uet.mobilecourse.assistant.model.firebase;


import com.google.firebase.firestore.Exclude;

public class UserInfo implements IFirebaseModel {

    private String id;
    private String name;
    private long DOB;
    private String uetClass;

    @Exclude
    private String avatar;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
