package vnu.uet.mobilecourse.assistant.model.forum;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;
import vnu.uet.mobilecourse.assistant.model.material.InternalFile;

import java.util.List;

@Entity
public class Post {
    @PrimaryKey
    @SerializedName("id")
    private int id;
    @SerializedName("discussionid")
    private int discussionId;
    @SerializedName("subject")
    private String subject;
    @SerializedName("message")
    private String message;
    private int authorId;
    private String authorName;
    @SerializedName("hasparent")
    private boolean isReply;
    @SerializedName("parentid")
    private int parentId;
    @SerializedName("timecreated")
    private long timeCreated;
    @Ignore
    @SerializedName("attachments")
    private List<InternalFile> attachments;
    @Ignore
    private List<Post> replies;

    public List<Post> getReplies() {
        return replies;
    }

    public void setReplies(List<Post> replies) {
        this.replies = replies;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDiscussionId() {
        return discussionId;
    }

    public void setDiscussionId(int discussionId) {
        this.discussionId = discussionId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public boolean getIsReply() {
        return isReply;
    }

    public void setIsReply(boolean isReply) {
        this.isReply = isReply;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public List<InternalFile> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<InternalFile> attachments) {
        this.attachments = attachments;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return "Post{" +
                ", replies=" + replies +
                '}';
    }
}
