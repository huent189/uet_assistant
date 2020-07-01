package vnu.uet.mobilecourse.assistant.adapter;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.noti.AdminNotificationHolder;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.noti.CourseNotificationHolder;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.noti.FinalExamNotificationHolder;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.noti.ForumNotificationHolder;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.noti.MaterialNotificationHolder;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.noti.NotificationHolder;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.noti.SubmissionNotificationHolder;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.noti.TodoNotificationHolder;
import vnu.uet.mobilecourse.assistant.model.notification.NotificationType;
import vnu.uet.mobilecourse.assistant.model.notification.Notification_UserSubCol;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationHolder> {

    private List<? extends Notification_UserSubCol> mNotifications;
    private Fragment mOwner;

    public NotificationAdapter(List<? extends Notification_UserSubCol> notifications, Fragment owner) {
        this.mNotifications = notifications;
        this.mOwner = owner;
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mOwner.getLayoutInflater()
                .inflate(R.layout.layout_notification_item, parent, false);

        NotificationHolder holder;

        switch (viewType) {
            case NotificationType.TODO:
                holder = new TodoNotificationHolder(view);
                break;

            case NotificationType.MATERIAL:
                holder = new MaterialNotificationHolder(view);
                break;

            case NotificationType.ATTENDANCE:
                holder = new CourseNotificationHolder(view);
                break;

            case NotificationType.FORUM:
                holder = new ForumNotificationHolder(view);
                break;

            case NotificationType.SUBMISSION:
                holder = new SubmissionNotificationHolder(view);
                break;

            case NotificationType.FINAL_EXAM:
                holder = new FinalExamNotificationHolder(view);
                break;

            default:
                holder = new AdminNotificationHolder(view);
                break;
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {
        final Notification_UserSubCol notification = mNotifications.get(position);
        holder.bind(notification, mOwner);
    }

    @Override
    public int getItemViewType(int position) {
        return mNotifications.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }
}
