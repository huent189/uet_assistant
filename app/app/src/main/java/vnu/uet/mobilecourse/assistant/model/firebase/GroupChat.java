package vnu.uet.mobilecourse.assistant.model.firebase;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

public class GroupChat implements IFirebaseModel {

    private String id;
    private long createdTime;

    @Exclude
    private String name;

    @Exclude
    private String avatar;

    @Exclude
    private List<Member_GroupChatSubCol> members = new ArrayList<>();

    @Exclude
    private List<Message_GroupChatSubCol> messages = new ArrayList<>();

    @Exclude
    public List<Member_GroupChatSubCol> getMembers() {
        return members;
    }

    @Exclude
    public List<Message_GroupChatSubCol> getMessages() {
        return messages;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    @Exclude
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Exclude
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static final String DIRECT = "DIRECT";
    public static final String GROUP = "GROUP";
}
