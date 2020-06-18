package vnu.uet.mobilecourse.assistant.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.forum.Post;
import vnu.uet.mobilecourse.assistant.repository.course.ForumRepository;

public class DiscussionViewModel extends ViewModel {

    private ForumRepository mForumRepo;

    public DiscussionViewModel() {
        mForumRepo = new ForumRepository();
    }

    public LiveData<Post> getDiscussionDetail(int discussionId) {
        return mForumRepo.getDiscussionDetail(discussionId);
    }
}
