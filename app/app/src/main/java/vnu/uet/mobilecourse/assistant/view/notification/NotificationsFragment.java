package vnu.uet.mobilecourse.assistant.view.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.NotificationAdapter;
import vnu.uet.mobilecourse.assistant.model.notification.Notification_UserSubCol;
import vnu.uet.mobilecourse.assistant.viewmodel.NotificationsViewModel;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel mViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        initializeNotificationListView(root);

        return root;
    }

    private void initializeNotificationListView(View root) {
        RecyclerView rvNotifications = root.findViewById(R.id.rvNotifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this.getContext()));

        View layoutEmpty = root.findViewById(R.id.layoutEmpty);

        ShimmerFrameLayout sflNotifications = root.findViewById(R.id.sflNotifications);
        sflNotifications.startShimmerAnimation();

        mViewModel.getNotifications().observe(getViewLifecycleOwner(), stateModel -> {
            switch (stateModel.getStatus()) {
                case LOADING:
                    sflNotifications.setVisibility(View.VISIBLE);
                    layoutEmpty.setVisibility(View.GONE);
                    rvNotifications.setVisibility(View.GONE);
                    break;

                case SUCCESS:
                    List<Notification_UserSubCol> notifications = stateModel.getData();

                    if (notifications.isEmpty()) {
                        layoutEmpty.setVisibility(View.VISIBLE);
                        rvNotifications.setVisibility(View.GONE);
                    } else {
                        NotificationAdapter adapter = new NotificationAdapter(notifications,
                                NotificationsFragment.this);
                        rvNotifications.setAdapter(adapter);

                        layoutEmpty.setVisibility(View.GONE);
                        rvNotifications.setVisibility(View.VISIBLE);
                    }

                    sflNotifications.setVisibility(View.GONE);

                    break;
            }
        });
    }
}
