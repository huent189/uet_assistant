package vnu.uet.mobilecourse.assistant.repository.firebase.chatnoti;

import com.google.gson.JsonObject;

public class Data {
    private String groupId;
    private String groupName;
    private String senderName;
    private String content;

    public Data(String groupId, String groupName, String senderName, String content) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.senderName = senderName;
        this.content = content;
    }

    public JsonObject toJSON(){
        JsonObject conv = new JsonObject();
        conv.addProperty("groupId", groupId);
        conv.addProperty("groupName", groupName);
        conv.addProperty("senderName", senderName);
        conv.addProperty("content", content);

        return conv;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
