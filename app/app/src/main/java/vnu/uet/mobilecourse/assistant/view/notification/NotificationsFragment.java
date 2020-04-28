package vnu.uet.mobilecourse.assistant.view.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.NotificationAdapter;
import vnu.uet.mobilecourse.assistant.viewmodel.NotificationsViewModel;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel viewModel;

    private NotificationAdapter notificationAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        initializeNotificationListView(root);

        return root;
    }

    private void initializeNotificationListView(View root) {
        List<String> notifications = new ArrayList<>();

        for (int i = 1; i < 20; i++) {
            notifications.add("Bài kiểm tra " + i);
        }

        notificationAdapter = new NotificationAdapter(notifications, this);

        RecyclerView rvNotifications = root.findViewById(R.id.rvNotifications);

        rvNotifications.setAdapter(notificationAdapter);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }
}
