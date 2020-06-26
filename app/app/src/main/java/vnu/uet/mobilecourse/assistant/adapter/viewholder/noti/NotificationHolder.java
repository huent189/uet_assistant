package vnu.uet.mobilecourse.assistant.adapter.viewholder.noti;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.notification.Notification_UserSubCol;

import static vnu.uet.mobilecourse.assistant.util.DateTimeUtils.generateViewText;

public abstract class NotificationHolder<T extends Notification_UserSubCol> extends RecyclerView.ViewHolder {

    private ImageView mIvNotifyIcon;
    private TextView mTvNotifyTitle;
    private TextView mTvNotifyDesc;
    private TextView mTvNotifyTime;
    private ImageButton mBtnViewNotify;

    protected NotificationHolder(@NonNull View view) {
        super(view);

        mIvNotifyIcon = view.findViewById(R.id.ivNotifyIcon);
        mTvNotifyTitle = view.findViewById(R.id.tvNotifyTitle);
        mTvNotifyDesc = view.findViewById(R.id.tvNotifyDesc);
        mTvNotifyTime = view.findViewById(R.id.tvNotifyTime);
        mBtnViewNotify = view.findViewById(R.id.btnViewNotify);
    }

    protected abstract int getIconResId();

    protected abstract void onNavigate(T notification, NavController navController, Fragment owner);


    public void bind(T notification, Fragment owner) {
        Activity activity = owner.getActivity();

        assert activity != null;
        NavController navController = Navigation
                .findNavController(activity, R.id.nav_host_fragment);

        String title = notification.getTitle();
        mTvNotifyTitle.setText(title);

        String desc = notification.getDescription();
        mTvNotifyDesc.setText(desc);

        String time = generateViewText(notification.getNotifyTime());
        mTvNotifyTime.setText(time);

        mIvNotifyIcon.setImageResource(getIconResId());

        mBtnViewNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigate(notification, navController, owner);
            }
        });
    }
}
