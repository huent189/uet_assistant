package vnu.uet.mobilecourse.assistant.view.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.NotificationAdapter;
import vnu.uet.mobilecourse.assistant.model.firebase.Notification_UserSubCol;
import vnu.uet.mobilecourse.assistant.viewmodel.NotificationsViewModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel mViewModel;

    private NotificationAdapter mNotificationAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        initializeNotificationListView(root);

        return root;
    }

    private void initializeNotificationListView(View root) {
        RecyclerView rvNotifications = root.findViewById(R.id.rvNotifications);

        rvNotifications.setAdapter(mNotificationAdapter);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this.getContext()));

        mViewModel.getNotifications().observe(getViewLifecycleOwner(), new Observer<StateModel<List<Notification_UserSubCol>>>() {
            @Override
            public void onChanged(StateModel<List<Notification_UserSubCol>> stateModel) {
                switch (stateModel.getStatus()) {
                    case SUCCESS:
                        List<Notification_UserSubCol> notifications = stateModel.getData();
                        mNotificationAdapter = new NotificationAdapter(notifications, NotificationsFragment.this);
                        rvNotifications.setAdapter(mNotificationAdapter);

                        break;
                }
            }
        });
    }
}
