package vnu.uet.mobilecourse.assistant.database.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import vnu.uet.mobilecourse.assistant.model.forum.Discussion;
import vnu.uet.mobilecourse.assistant.model.forum.Post;
import vnu.uet.mobilecourse.assistant.model.material.CourseConstant;

import java.util.List;

@Dao
public abstract class ForumDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertDiscussion(List<Discussion> discussions);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertPost(List<Post> posts);
    @Query("SELECT COALESCE(MAX(timeModified), -1) from Discussion WHERE forumId = :forumId")
    public abstract long getDiscussionLastUpdate(int forumId);
    @Query("SELECT COALESCE(MAX(timeCreated), -1) from Post WHERE discussionId = :discussionId")
    public abstract long getDiscussionPostLastUpdate(int discussionId);
    @Query("SELECT * FROM Post WHERE discussionId = :discussionId")
    public abstract LiveData<List<Post>> getDiscussionPost(int discussionId);
    @Query("SELECT * FROM Discussion WHERE forumId = :forumId")
    public abstract LiveData<List<Discussion>> getDiscussionByForum(int forumId);
    @Query("SELECT instanceId FROM Material WHERE type = '" + CourseConstant.MaterialType.FORUM +"'")
    public abstract int[] getAllForumId();
    @Query("SELECT * FROM Discussion WHERE id = :discussionId")
    public abstract LiveData<Discussion> getDiscussionById(int discussionId);
}
