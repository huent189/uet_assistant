package vnu.uet.mobilecourse.assistant.model.forum;

import com.google.firebase.firestore.Exclude;

import vnu.uet.mobilecourse.assistant.model.firebase.IFirebaseModel;

public class InterestedDiscussion implements IFirebaseModel {

    private String id;
    private int discussionId;
    private long time;

    @Exclude
    private Discussion discussion;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDiscussionId() {
        return discussionId;
    }

    public void setDiscussionId(int discussionId) {
        this.discussionId = discussionId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Exclude
    public Discussion getDiscussion() {
        return discussion;
    }

    @Exclude
    public void setDiscussion(Discussion discussion) {
        this.discussion = discussion;
    }
}
