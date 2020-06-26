package vnu.uet.mobilecourse.assistant.adapter.viewholder.noti;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.notification.AdminNotification;

public class AdminNotificationHolder extends NotificationHolder<AdminNotification> {

    public AdminNotificationHolder(@NonNull View view) {
        super(view);
    }

    @Override
    protected int getIconResId() {
        return R.drawable.img_admin_bot;
    }

    @Override
    protected void onNavigate(AdminNotification notification, NavController navController, Fragment owner) {

    }
}
