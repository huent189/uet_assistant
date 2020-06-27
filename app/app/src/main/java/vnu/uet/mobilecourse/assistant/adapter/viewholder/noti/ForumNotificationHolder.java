package vnu.uet.mobilecourse.assistant.adapter.viewholder.noti;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.forum.Discussion;
import vnu.uet.mobilecourse.assistant.model.notification.ForumPostNotification;
import vnu.uet.mobilecourse.assistant.repository.course.ForumRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class ForumNotificationHolder extends NotificationHolder<ForumPostNotification> {

    public ForumNotificationHolder(@NonNull View view) {
        super(view);
    }

    @Override
    protected int getIconResId() {
        return R.drawable.img_material;
    }

    @Override
    protected void onNavigate(ForumPostNotification notification, NavController navController, Fragment owner) {
        IStateLiveData<Discussion> discussionState = ForumRepository.getInstance()
                .getDiscussionById(notification.getDiscussionId());

        discussionState.observe(owner.getViewLifecycleOwner(), new Observer<StateModel<Discussion>>() {
                    @Override
                    public void onChanged(StateModel<Discussion> stateModel) {
                        switch (stateModel.getStatus()) {
                            case SUCCESS:
                                discussionState.removeObserver(this);

                                Bundle bundle = new Bundle();
                                bundle.putString("forum", "Thông báo");
                                Discussion discussion = stateModel.getData();
                                bundle.putParcelable("discussion", discussion);
                                navController.navigate(R.id.action_navigation_notifications_to_navigation_discussion, bundle);

                                break;

                            case ERROR:
                                discussionState.removeObserver(this);

                                Context context = owner.getContext();
                                final String DISCUSSION_NOT_FOUND_MSG = "Không tìm thấy cuộc thảo luận";
                                Toast.makeText(context, DISCUSSION_NOT_FOUND_MSG, Toast.LENGTH_SHORT).show();

                                break;
                        }
                    }
                });
    }
}
