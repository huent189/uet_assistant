package vnu.uet.mobilecourse.assistant.model.notification;

public class ForumPostNotification extends Notification_UserSubCol {

    private int discussionId;

    public int getDiscussionId() {
        return discussionId;
    }

    public void setDiscussionId(int discussionId) {
        this.discussionId = discussionId;
    }

    public ForumPostNotification() {
        super(NotificationType.FORUM);
    }
}
