package vnu.uet.mobilecourse.assistant.ui.adapter;

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
    private static final String TAG = NotificationAdapter.class.getSimpleName();

    private List<String> notifications;

    private Fragment owner;

    private NavController navController;

    public NotificationAdapter(List<String> notifications, Fragment owner) {
        this.notifications = notifications;
        this.owner = owner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = owner.getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.layout_notification_item, parent, false);

        ViewHolder holder = new ViewHolder(view);

        Activity activity = owner.getActivity();

        if (activity != null)
            navController = Navigation.findNavController(activity, R.id.nav_host_fragment);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "call onBindViewHolder");
    }


    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivNotifyIcon;

        private TextView tvNotifyTitle;

        private TextView tvNotifyDesc;

        private TextView tvNotifyTime;

        private Button btnViewNotify;

        public ViewHolder(@NonNull View view) {
            super(view);

            ivNotifyIcon = view.findViewById(R.id.ivNotifyIcon);
            tvNotifyTitle = view.findViewById(R.id.tvNotifyTitle);
            tvNotifyDesc = view.findViewById(R.id.tvNotifyDesc);
            tvNotifyTime = view.findViewById(R.id.tvNotifyTime);
            btnViewNotify = view.findViewById(R.id.btnViewNotify);

        }
    }
}
