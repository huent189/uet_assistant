package vnu.uet.mobilecourse.assistant.model.firebase;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

import vnu.uet.mobilecourse.assistant.model.IStudent;

public class Member_GroupChatSubCol implements IFirebaseModel, IStudent, Parcelable {
    /**
     * member id
     */
    private String id;
    private String name;
    private String role;
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Exclude
    private String avatar;

    public Member_GroupChatSubCol() {

    }

    protected Member_GroupChatSubCol(Parcel in) {
        id = in.readString();
        name = in.readString();
        role = in.readString();
        avatar = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(role);
        dest.writeString(avatar);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Member_GroupChatSubCol> CREATOR = new Creator<Member_GroupChatSubCol>() {
        @Override
        public Member_GroupChatSubCol createFromParcel(Parcel in) {
            return new Member_GroupChatSubCol(in);
        }

        @Override
        public Member_GroupChatSubCol[] newArray(int size) {
            return new Member_GroupChatSubCol[size];
        }
    };

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
