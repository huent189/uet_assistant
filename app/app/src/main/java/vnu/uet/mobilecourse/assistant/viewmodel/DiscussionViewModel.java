package vnu.uet.mobilecourse.assistant.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.forum.InterestedDiscussion;
import vnu.uet.mobilecourse.assistant.model.forum.Post;
import vnu.uet.mobilecourse.assistant.repository.course.ForumRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public class DiscussionViewModel extends ViewModel {

    private ForumRepository mForumRepo;

    public DiscussionViewModel() {
        mForumRepo = ForumRepository.getInstance();
    }

    public LiveData<Post> getDiscussionDetail(int discussionId) {
        return mForumRepo.getDiscussionDetail(discussionId);
    }

    public IStateLiveData<InterestedDiscussion> follow(int discussionId) {
        return mForumRepo.follow(discussionId);
    }

    public IStateLiveData<String> unFollow(int discussionId) {
        return mForumRepo.unFollow(discussionId);
    }
}
