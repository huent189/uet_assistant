package vnu.uet.mobilecourse.assistant.viewmodel;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.forum.Discussion;
import vnu.uet.mobilecourse.assistant.repository.course.ForumRepository;

public class ForumViewModel extends ViewModel {

    private ForumRepository forumRepo;

    public ForumViewModel() {
        forumRepo = new ForumRepository();
    }

    public LiveData<List<Discussion>> getDiscussions(int instanceId) {
        return forumRepo.getDiscussionsByForum(instanceId);
    }
}
