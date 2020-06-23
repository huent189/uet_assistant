package vnu.uet.mobilecourse.assistant.model.firebase;

import com.google.firebase.firestore.Exclude;

import vnu.uet.mobilecourse.assistant.model.IStudent;

public class Member_GroupChatSubCol implements IFirebaseModel, IStudent {
    /**
     * member id
     */
    private String id;
    private String name;
    private String role;

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

    @Override
    public String getCode() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Exclude
    public String getAvatar() {
        return avatar;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Exclude
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
