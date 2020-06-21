package vnu.uet.mobilecourse.assistant.viewmodel;

import java.util.List;

import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.forum.Discussion;
import vnu.uet.mobilecourse.assistant.model.forum.InterestedDiscussion;
import vnu.uet.mobilecourse.assistant.repository.course.ForumRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public class ForumViewModel extends ViewModel {

    private ForumRepository mForumRepo;

    public ForumViewModel() {
        mForumRepo = ForumRepository.getInstance();
    }

    public IStateLiveData<List<Discussion>> getDiscussions(int instanceId) {
        return mForumRepo.getDiscussionsByForum(instanceId);
    }

    public IStateLiveData<InterestedDiscussion> follow(int discussionId) {
        return mForumRepo.follow(discussionId);
    }

    public IStateLiveData<String> unFollow(int discussionId) {
        return mForumRepo.unFollow(discussionId);
    }
}
