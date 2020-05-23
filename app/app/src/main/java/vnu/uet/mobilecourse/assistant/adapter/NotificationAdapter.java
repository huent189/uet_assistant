package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<String> mNotifications;
    private Fragment mOwner;
    private NavController mNavController;

    public NotificationAdapter(List<String> notifications, Fragment owner) {
        this.mNotifications = notifications;
        this.mOwner = owner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mOwner.getLayoutInflater()
                .inflate(R.layout.layout_notification_item, parent, false);

        Activity activity = mOwner.getActivity();

        if (activity != null) {
            mNavController = Navigation
                    .findNavController(activity, R.id.nav_host_fragment);
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mIvNotifyIcon;

        private TextView mTvNotifyTitle;

        private TextView mTvNotifyDesc;

        private TextView mTvNotifyTime;

        private Button mBtnViewNotify;

        public ViewHolder(@NonNull View view) {
            super(view);

            mIvNotifyIcon = view.findViewById(R.id.ivNotifyIcon);
            mTvNotifyTitle = view.findViewById(R.id.tvNotifyTitle);
            mTvNotifyDesc = view.findViewById(R.id.tvNotifyDesc);
            mTvNotifyTime = view.findViewById(R.id.tvNotifyTime);
            mBtnViewNotify = view.findViewById(R.id.btnViewNotify);

        }
    }
}
