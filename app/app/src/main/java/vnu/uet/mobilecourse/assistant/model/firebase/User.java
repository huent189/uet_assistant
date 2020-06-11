package vnu.uet.mobilecourse.assistant.model.firebase;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

import vnu.uet.mobilecourse.assistant.model.firebase.notification.Notification_UserSubCol;

public class User implements IFirebaseModel {

    private String id;
    private String avatar;
    private int newNotifications;

    @Exclude
    private int unseenGroupChat;

    @Exclude
    private String name;

    @Exclude
    private List<GroupChat_UserSubCol> groupChats = new ArrayList<>();

    @Exclude
    private List<Notification_UserSubCol> notifications = new ArrayList<>();

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Exclude
    public List<GroupChat_UserSubCol> getGroupChats() {
        return groupChats;
    }

    public void setGroupChats(List<GroupChat_UserSubCol> groupChats) {
        this.groupChats = groupChats;
    }

    @Exclude
    public List<Notification_UserSubCol> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification_UserSubCol> notifications) {
        this.notifications = notifications;
    }

    @Exclude
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNewNotifications() {
        return newNotifications;
    }

    public void setNewNotifications(int newNotifications) {
        this.newNotifications = newNotifications;
    }

    @Exclude
    public int getUnseenGroupChat() {
        return unseenGroupChat;
    }

    public void setUnseenGroupChat(int unseenGroupChat) {
        this.unseenGroupChat = unseenGroupChat;
    }
}
