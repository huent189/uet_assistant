package vnu.uet.mobilecourse.assistant.adapter.viewholder.noti;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.notification.ForumPostNotification;
import vnu.uet.mobilecourse.assistant.model.notification.SubmissionNotification;

public class SubmissionNotificationHolder extends NotificationHolder<SubmissionNotification> {

    public SubmissionNotificationHolder(@NonNull View view) {
        super(view);
    }

    @Override
    protected int getIconResId() {
        return R.drawable.img_material;
    }

    @Override
    protected void onNavigate(SubmissionNotification notification, NavController navController, Fragment owner) {

    }
}
