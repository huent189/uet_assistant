package vnu.uet.mobilecourse.assistant.model.firebase.notification;

public class AdminNotification extends Notification_UserSubCol {

    private int thumbnail;

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int resId) {
        this.thumbnail = resId;
    }
}
