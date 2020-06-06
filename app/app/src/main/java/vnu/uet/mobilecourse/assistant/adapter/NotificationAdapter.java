package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.ISwipeToDeleteHolder;
import vnu.uet.mobilecourse.assistant.model.firebase.NotificationType;
import vnu.uet.mobilecourse.assistant.model.firebase.Notification_UserSubCol;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {

    private List<Notification_UserSubCol> mNotifications;
    private Fragment mOwner;
    private NavController mNavController;

    public NotificationAdapter(List<Notification_UserSubCol> notifications, Fragment owner) {
        this.mNotifications = notifications;
        this.mOwner = owner;
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mOwner.getLayoutInflater()
                .inflate(R.layout.layout_notification_item, parent, false);

        Activity activity = mOwner.getActivity();

        if (activity != null) {
            mNavController = Navigation
                    .findNavController(activity, R.id.nav_host_fragment);
        }

        return new NotificationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {
        final Notification_UserSubCol notification = mNotifications.get(position);
        holder.bind(notification);
    }

    public List<Notification_UserSubCol> getNotifications() {
        return mNotifications;
    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public static class NotificationHolder extends RecyclerView.ViewHolder implements ISwipeToDeleteHolder {

        private ImageView mIvNotifyIcon;
        private TextView mTvNotifyTitle;
        private TextView mTvNotifyDesc;
        private TextView mTvNotifyTime;
        private ImageButton mBtnViewNotify;

        public NotificationHolder(@NonNull View view) {
            super(view);

            mIvNotifyIcon = view.findViewById(R.id.ivNotifyIcon);
            mTvNotifyTitle = view.findViewById(R.id.tvNotifyTitle);
            mTvNotifyDesc = view.findViewById(R.id.tvNotifyDesc);
            mTvNotifyTime = view.findViewById(R.id.tvNotifyTime);
            mBtnViewNotify = view.findViewById(R.id.btnViewNotify);

        }

        public void bind(Notification_UserSubCol notification) {
            String title = notification.getTitle();
            mTvNotifyTitle.setText(title);

            String desc = notification.getDescription();
            mTvNotifyDesc.setText(desc);

            Date notifyTime = DateTimeUtils.fromSecond(notification.getNotifyTime());
            String time = DateTimeUtils.DATE_TIME_FORMAT.format(notifyTime);
            mTvNotifyTime.setText(time);

            switch (notification.getType()) {
                case NotificationType.TODO:
                    String todoId = notification.getReference();
                    mBtnViewNotify.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO: navigate to view todo info
                        }
                    });
                    break;

                case NotificationType.MATERIAL:
                    String materialId = notification.getReference();
                    mBtnViewNotify.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO: navigate to view material info
                        }
                    });
                    break;

                case NotificationType.ATTENDANCE:
                    String courseId = notification.getReference();
                    mBtnViewNotify.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO: navigate to view course info
                        }
                    });
                    break;
            }
        }
    }
}
