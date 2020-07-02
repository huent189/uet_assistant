package vnu.uet.mobilecourse.assistant.repository.course;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import com.google.gson.JsonElement;
import retrofit2.Call;
import vnu.uet.mobilecourse.assistant.database.CoursesDatabase;
import vnu.uet.mobilecourse.assistant.database.DAO.ForumDAO;
import vnu.uet.mobilecourse.assistant.database.DAO.InterestedDiscussionDAO;
import vnu.uet.mobilecourse.assistant.exception.DocumentNotFoundException;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.forum.Discussion;
import vnu.uet.mobilecourse.assistant.model.forum.DiscussionComparator;
import vnu.uet.mobilecourse.assistant.model.forum.InterestedDiscussion;
import vnu.uet.mobilecourse.assistant.model.forum.Post;
import vnu.uet.mobilecourse.assistant.network.CourseClient;
import vnu.uet.mobilecourse.assistant.network.request.CourseRequest;
import vnu.uet.mobilecourse.assistant.network.response.CoursesResponseCallback;
import vnu.uet.mobilecourse.assistant.util.FirebaseStructureId;
import vnu.uet.mobilecourse.assistant.viewmodel.state.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ForumRepository {

    private CourseRequest sender;
    private ForumDAO forumDAO;
    private InterestedDiscussionDAO interestDAO;
    private static ForumRepository instance;

    public ForumRepository() {
        sender = CourseClient.getInstance().request(CourseRequest.class);
        forumDAO = CoursesDatabase.getDatabase().forumDAO();
        interestDAO = new InterestedDiscussionDAO();
    }

    public static ForumRepository getInstance() {
        if (instance == null) {
            instance = new ForumRepository();
        }

        return instance;
    }
    public IStateLiveData<List<Discussion>> getDiscussionsByForum(int forumInstanceId) {
        new Thread(() -> {
            try {
                updateDiscussionByForum(forumInstanceId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();


        LiveData<List<Discussion>> discussions = forumDAO.getDiscussionByForum(forumInstanceId);
        StateLiveData<List<InterestedDiscussion>> interests = interestDAO.readAll();

        return new MergeDiscussion(discussions, interests);
    }
    public IStateLiveData<Discussion> getDiscussionById (int discussionId){
        LiveData<Discussion> discussion = forumDAO.getDiscussionById(discussionId);
        new Thread(() -> {
            try {
                updatePostByDiscussion(discussionId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        StateMediatorLiveData<InterestedDiscussion> interest = getFollowingDiscussion(discussionId);
        StateMediatorLiveData<Discussion> mergeLiveData = new StateMediatorLiveData<>();
        mergeLiveData.postLoading();
        mergeLiveData.addSource(discussion, new Observer<Discussion>() {
            @Override
            public void onChanged(Discussion discussion) {
                if(discussion != null){
                    StateModel<Discussion> old = mergeLiveData.getValue();
                    if(old != null && old.getStatus() != StateStatus.ERROR){
                        StateStatus status = StateStatus.LOADING;
                        if(old.getData() != null){
                            discussion.setInterest(old.getData().isInterest());
                            status = StateStatus.SUCCESS;
                        }
                        mergeLiveData.postValue(new StateModel<>(status, discussion));
                    }
                }
            }
        });
        mergeLiveData.addSource(interest, new Observer<StateModel<InterestedDiscussion>>() {
            @Override
            public void onChanged(StateModel<InterestedDiscussion> interestedDiscussionStateModel) {
                switch (interestedDiscussionStateModel.getStatus()){
                    case LOADING:
                        mergeLiveData.postLoading();
                        break;
                    case ERROR:
                        if(interestedDiscussionStateModel.getError() instanceof DocumentNotFoundException){
                            StateModel<Discussion> old = mergeLiveData.getValue();
                            if(old != null){
                                StateStatus status = StateStatus.SUCCESS;
                                Discussion d = old.getData();
                                if(d == null){
                                    d = new Discussion();
                                    status = StateStatus.LOADING;
                                }
                                d.setInterest(false);
                                mergeLiveData.postValue(new StateModel<>(status, d));
                            }
                        }
                        else {
                            mergeLiveData.postError(interestedDiscussionStateModel.getError());
                        }
                        break;
                    case SUCCESS:
                        StateModel<Discussion> old = mergeLiveData.getValue();
                        if(old != null){
                            StateStatus status = StateStatus.SUCCESS;
                            Discussion d = old.getData();
                            if(d == null){
                                d = new Discussion();
                                status = StateStatus.LOADING;
                            }
                            d.setInterest(true);
                            mergeLiveData.postValue(new StateModel<>(status, d));
                        }
                        break;
                }
            }
        });
        return mergeLiveData;
    }
    public StateMediatorLiveData<InterestedDiscussion> getFollowingDiscussion(int discussionId) {
        String formatId = FirebaseStructureId.interestDiscussion(discussionId);
        return interestDAO.read(formatId);
    }
    public List<Discussion> updateAllDiscussion() throws IOException {
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
                if(lastTime != -1 ){
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
                else {
                    forumDAO.insertDiscussion(Arrays.asList(response));
                }
            }
        };
        handler.onResponse(call, call.execute());
        return updateList;
    }

    public List<Post> updatePostByDiscussion(int discussionId) throws IOException {
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
                forumDAO.insertPost(updateList);
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

    public IStateLiveData<List<InterestedDiscussion>> getFollowingDiscussions() {
        return interestDAO.readAll();
    }

    public IStateLiveData<InterestedDiscussion> follow(int discussionId) {
        String docId = FirebaseStructureId.interestDiscussion(discussionId);

        InterestedDiscussion interest = new InterestedDiscussion();
        interest.setId(docId);
        interest.setDiscussionId(discussionId);
        interest.setTime(System.currentTimeMillis() / 1000);

        return interestDAO.add(docId, interest);
    }



    public IStateLiveData<String> unFollow(int discussionId) {
        String docId = FirebaseStructureId.interestDiscussion(discussionId);
        return interestDAO.delete(docId);
    }

    private static final String STUDENT_ID = User.getInstance().getStudentId();

    static class MergeDiscussion extends StateMediatorLiveData<List<Discussion>> {

        private List<Discussion> mDiscussions;
        private List<InterestedDiscussion> mInterests;
        private boolean courseSuccess;
        private boolean firebaseSuccess;

        MergeDiscussion(LiveData<List<Discussion>> discussions, StateLiveData<List<InterestedDiscussion>> interests) {
            postLoading();

            addSource(discussions, new Observer<List<Discussion>>() {
                @Override
                public void onChanged(List<Discussion> discussions) {
                    if (discussions == null) {
                        postLoading();
                        courseSuccess = false;
                    } else {
                        courseSuccess = true;
                        setDiscussions(discussions);

                        if (courseSuccess && firebaseSuccess) {
                            List<Discussion> combineData = combineData();
                            postSuccess(combineData);
                        }
                    }
                }
            });

            addSource(interests, new Observer<StateModel<List<InterestedDiscussion>>>() {
                @Override
                public void onChanged(StateModel<List<InterestedDiscussion>> stateModel) {
                    switch (stateModel.getStatus()) {
                        case LOADING:
                            postLoading();
                            firebaseSuccess = false;
                            break;

                        case ERROR:
                            postError(stateModel.getError());
                            firebaseSuccess = false;
                            break;

                        case SUCCESS:
                            firebaseSuccess = true;
                            setInterests(stateModel.getData());

                            if (courseSuccess && firebaseSuccess) {
                                List<Discussion> combineData = combineData();
                                postSuccess(combineData);
                            }

                            break;
                    }
                }
            });
        }


        private void setInterests(List<InterestedDiscussion> interests) {
            this.mInterests = interests;
        }

        private void setDiscussions(List<Discussion> mDiscussions) {
            this.mDiscussions = mDiscussions;
        }

        private List<Discussion> combineData() {
            List<Discussion> discussions = new ArrayList<>(mDiscussions);
            List<InterestedDiscussion> interests = new ArrayList<>(mInterests);

            interests.forEach(i -> Log.d("INTEREST", "interest: " + i.getDiscussionId()));

            discussions.forEach(discussion -> {
                boolean found = false;

                for (InterestedDiscussion interest : interests) {
                    if (interest.getDiscussionId() == discussion.getId()) {
                        found = true;
                        interests.remove(interest);
                        break;
                    }
                }

                discussion.setInterest(found);
            });

            discussions.sort(new DiscussionComparator());

            return discussions;
        }
    }

    public synchronized  List<InterestedDiscussion> getInterestedSynchronize() throws ExecutionException, InterruptedException {
        return interestDAO.getAllSynchronize();
    }
}

