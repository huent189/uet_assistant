package vnu.uet.mobilecourse.assistant.model.forum;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity()
public class Discussion implements Parcelable {
    @PrimaryKey
    @SerializedName("discussion")
    private int id;
    private int forumId;
    @SerializedName("name")
    private String name;
    @SerializedName("created")
    private long timeCreated;
    @SerializedName("timemodified")
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
    private String message;
    @Ignore
    private boolean interest;

    public Discussion() {

    }

    protected Discussion(Parcel in) {
        id = in.readInt();
        forumId = in.readInt();
        name = in.readString();
        timeCreated = in.readLong();
        timeModified = in.readLong();
        authorName = in.readString();
        authorId = in.readString();
        isPinned = in.readByte() != 0;
        isLocked = in.readByte() != 0;
        isStarred = in.readByte() != 0;
        numberReplies = in.readInt();
        message = in.readString();
        interest = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(forumId);
        dest.writeString(name);
        dest.writeLong(timeCreated);
        dest.writeLong(timeModified);
        dest.writeString(authorName);
        dest.writeString(authorId);
        dest.writeByte((byte) (isPinned ? 1 : 0));
        dest.writeByte((byte) (isLocked ? 1 : 0));
        dest.writeByte((byte) (isStarred ? 1 : 0));
        dest.writeInt(numberReplies);
        dest.writeString(message);
        dest.writeByte((byte) (interest ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Discussion> CREATOR = new Creator<Discussion>() {
        @Override
        public Discussion createFromParcel(Parcel in) {
            return new Discussion(in);
        }

        @Override
        public Discussion[] newArray(int size) {
            return new Discussion[size];
        }
    };

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

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

    public boolean isInterest() {
        return interest;
    }

    public void setInterest(boolean interest) {
        this.interest = interest;
    }

    @NonNull
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
                ", follow=" + interest +
                '}';
    }
}
