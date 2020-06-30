package vnu.uet.mobilecourse.assistant.model.firebase;

import com.google.firebase.firestore.Exclude;

import vnu.uet.mobilecourse.assistant.util.FirebaseStructureId;

public class GroupChat_UserSubCol implements IFirebaseModel {

    private String id;
    private String name;
    private long avatar;
    private String lastMessage;
    private long lastMessageTime;
    private boolean seen;

    @Exclude
    private String type;

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

    public long getAvatar() {
        return avatar;
    }

    public void setAvatar(long avatar) {
        this.avatar = avatar;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getType() {
        if (type == null) {
            type = FirebaseStructureId.isDirectedChat(id) ? GroupChat.DIRECT : GroupChat.GROUP;
        }

        return type;
    }
}
