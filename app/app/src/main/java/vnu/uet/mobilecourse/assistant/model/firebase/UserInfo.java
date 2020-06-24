package vnu.uet.mobilecourse.assistant.model.firebase;


import android.os.Parcel;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import androidx.annotation.NonNull;
import vnu.uet.mobilecourse.assistant.model.IStudent;

public class UserInfo implements IFirebaseModel, IStudent {

    @PropertyName("stuentId")
    private String id;

    private String name;

    @PropertyName("dob")
    private long DOB;

    private String uetClass;

    @Exclude
    private String avatar;

    @Exclude
    private boolean active;

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

    @Override
    public String getCode() {
        return id;
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

    @Exclude
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @NonNull
    @Override
    public UserInfo clone() {
        UserInfo clone = new UserInfo();
        clone.setId(id);
        clone.setName(name);
        clone.setDOB(DOB);
        clone.setUetClass(uetClass);

        return clone;
    }
}
