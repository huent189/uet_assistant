package vnu.uet.mobilecourse.assistant.model.firebase;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import androidx.annotation.NonNull;
import vnu.uet.mobilecourse.assistant.model.IStudent;

public class Participant_CourseSubCol implements IFirebaseModel, IStudent {

    @PropertyName("studentId")
    private String id;

    private String name;

    private int group;

    @Exclude
    private String avatar;

    @Exclude
    private boolean active;

    @Override
    @PropertyName("studentId")
    public String getId() {
        return id;
    }

    @PropertyName("studentId")
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getCode() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    @Exclude
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
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
    public Participant_CourseSubCol clone() {
        Participant_CourseSubCol clone = new Participant_CourseSubCol();
        clone.setId(id);
        clone.setName(name);
        clone.setGroup(group);
        return clone;
    }
}
