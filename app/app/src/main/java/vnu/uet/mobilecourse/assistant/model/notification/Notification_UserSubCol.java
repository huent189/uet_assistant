package vnu.uet.mobilecourse.assistant.model.notification;

import vnu.uet.mobilecourse.assistant.model.firebase.IFirebaseModel;

public abstract class Notification_UserSubCol implements IFirebaseModel {

    private String id;
//    private String category;
    private String title;
    private String description;
    private long notifyTime;
    private int type;
//    private String reference;

    public Notification_UserSubCol() {

    }

    public Notification_UserSubCol(int type) {
        this.type = type;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(long notifyTime) {
        this.notifyTime = notifyTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

//    public String getReference() {
//        return reference;
//    }
//
//    public void setReference(String reference) {
//        this.reference = reference;
//    }
//
//    public String getCategory() {
//        return category;
//    }
//
//    public void setCategory(String category) {
//        this.category = category;
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
