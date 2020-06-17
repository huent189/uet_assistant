package vnu.uet.mobilecourse.assistant.model.forum;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

@Entity()
public class Discussion {
    @PrimaryKey
    @SerializedName("discussion")
    private int id;
    private int forumId;
    @SerializedName("name")
    private String name;
    @SerializedName("created")
    private long timeCreated;
    @SerializedName("modified")
    private long timeModified;
    @SerializedName("userfullname")
    private String authorName;
    @SerializedName("userid")
    private String authorId;
    @SerializedName("pinned")
    private boolean isPinned;
    @SerializedName("locked")
    private boolean isLocked;
    @SerializedName("starred")
    private boolean isStarred;
    @SerializedName("numreplies")
    private int numberReplies;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getForumId() {
        return forumId;
    }

    public void setForumId(int forumId) {
        this.forumId = forumId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public long getTimeModified() {
        return timeModified;
    }

    public void setTimeModified(long timeModified) {
        this.timeModified = timeModified;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public boolean isStarred() {
        return isStarred;
    }

    public void setStarred(boolean starred) {
        isStarred = starred;
    }

    public int getNumberReplies() {
        return numberReplies;
    }

    public void setNumberReplies(int numberReplies) {
        this.numberReplies = numberReplies;
    }

    @Override
    public String toString() {
        return "Discussion{" +
                "id=" + id +
                ", forumId=" + forumId +
                ", name='" + name + '\'' +
                ", timeCreated=" + timeCreated +
                ", timeModified=" + timeModified +
                ", authorName='" + authorName + '\'' +
                ", authorId='" + authorId + '\'' +
                ", isPinned=" + isPinned +
                ", isLocked=" + isLocked +
                ", isStarred=" + isStarred +
                ", numberReplies=" + numberReplies +
                '}';
    }
}
