package vnu.uet.mobilecourse.assistant.repository.course;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import com.google.gson.JsonElement;
import retrofit2.Call;
import vnu.uet.mobilecourse.assistant.database.CoursesDatabase;
import vnu.uet.mobilecourse.assistant.database.DAO.ForumDAO;
import vnu.uet.mobilecourse.assistant.model.forum.Discussion;
import vnu.uet.mobilecourse.assistant.model.forum.Post;
import vnu.uet.mobilecourse.assistant.network.HTTPClient;
import vnu.uet.mobilecourse.assistant.network.request.CourseRequest;
import vnu.uet.mobilecourse.assistant.network.response.CoursesResponseCallback;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ForumRepository {
    private CourseRequest sender;
    private ForumDAO forumDAO;

    public ForumRepository() {
        sender = HTTPClient.getInstance().request(CourseRequest.class);
        forumDAO = CoursesDatabase.getDatabase().forumDAO();
    }
    public LiveData<List<Discussion>> getDiscussionsByForum(int forumInstanceId){
        new Thread(() -> {
            try {
                updateDiscussionByForum(forumInstanceId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        return forumDAO.getDiscussionByForum(forumInstanceId);
    }

    public List<Discussion> updateAllDiccussion() throws IOException {
        int[] forumIds = forumDAO.getAllForumId();
        ArrayList<Discussion> discussions = new ArrayList<>();
        for(int id: forumIds){
            discussions.addAll(updateDiscussionByForum(id));
        }
        return discussions;
    }
    private List<Discussion> updateDiscussionByForum(int forumId) throws IOException {
        Call<JsonElement> call = sender.getDiscussionByForum(forumId);
        final ArrayList<Discussion> updateList = new ArrayList<>();
        CoursesResponseCallback<Discussion[]> handler
                = new CoursesResponseCallback<Discussion[]>(Discussion[].class, "discussions") {
            @Override
            public void onSuccess(Discussion[] response) {
                long lastTime = forumDAO.getDiscussionLastUpdate(forumId);
                updateList.addAll(Arrays.stream(response)
                        .filter(p -> p.getTimeModified() > lastTime).collect(Collectors.toList()));
                updateList.forEach(new Consumer<Discussion>() {
                    @Override
                    public void accept(Discussion discussion) {
                        discussion.setForumId(forumId);
                    }
                });
                forumDAO.insertDiscussion(updateList);
            }
        };
        handler.onResponse(call, call.execute());
        return updateList;
    }

    private List<Post> updatePostByDiscussion(int discussionId) throws IOException {
        Call<JsonElement> call = sender.getDiscussionDetail(discussionId);
        final ArrayList<Post> updateList = new ArrayList<>();
        CoursesResponseCallback<Post[]> handler
                = new CoursesResponseCallback<Post[]>(Post[].class, "posts") {
            @Override
            public void onSuccess(Post[] response) {
                long lastTime = forumDAO.getDiscussionPostLastUpdate(discussionId);
                Log.d("REPO_UPDATE", "onSuccess: " + lastTime);
                updateList.addAll(Arrays.stream(response)
                        .filter(p -> p.getTimeCreated() > lastTime).collect(Collectors.toList()));
                forumDAO.insertPost(Arrays.asList(response));
            }
        };
        handler.onResponse(call, call.execute());
        return updateList;
    }
    public LiveData<Post> getDiscussionDetail(int discussionId){
        new Thread(() -> {
            try {
                updatePostByDiscussion(discussionId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        LiveData<List<Post>> posts = forumDAO.getDiscussionPost(discussionId);
        LiveData<Post> hierarchicalPost = Transformations.map(posts, postList -> {
            postList.forEach((cur) -> {
                cur.setReplies(postList.stream().filter(post -> post.getParentId() == cur.getId()).collect(Collectors.toList()));
            });
            return postList.stream().filter(post -> !post.getIsReply()).findFirst().orElse(null);
        });
        return hierarchicalPost;
    }

    public static class MergeDiscussion extends StateMediatorLiveData<List<Discussion>> {

        public MergeDiscussion(LiveData<List<Discussion>> liveData) {
            postLoading();

            addSource(liveData, new Observer<List<Discussion>>() {
                @Override
                public void onChanged(List<Discussion> discussions) {
                    if (discussions == null) {
                        postLoading();
                    }
                    else {
                        discussions.forEach(discussion -> {
//                            d
                        });
                    }
                }
            });
        }

    }
}

