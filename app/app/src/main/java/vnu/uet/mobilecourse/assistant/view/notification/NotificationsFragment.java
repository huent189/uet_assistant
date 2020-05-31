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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.NotificationAdapter;
import vnu.uet.mobilecourse.assistant.model.firebase.Notification_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.view.component.SwipeToDeleteCallback;
import vnu.uet.mobilecourse.assistant.viewmodel.NotificationsViewModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel mViewModel;

    private RecyclerView mRvNotifications;
    private NotificationAdapter mNotificationAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        initializeNotificationListView(root);

        enableSwipeToDelete();

        return root;
    }

    private void enableSwipeToDelete() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final Notification_UserSubCol item = mNotificationAdapter.getNotifications().get(position);

                mViewModel.deleteNotification(item);
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(mRvNotifications);
    }

    private void initializeNotificationListView(View root) {
        mRvNotifications = root.findViewById(R.id.rvNotifications);

        mRvNotifications.setAdapter(mNotificationAdapter);
        mRvNotifications.setLayoutManager(new LinearLayoutManager(this.getContext()));

        mViewModel.getNotifications().observe(getViewLifecycleOwner(), new Observer<StateModel<List<Notification_UserSubCol>>>() {
            @Override
            public void onChanged(StateModel<List<Notification_UserSubCol>> stateModel) {
                switch (stateModel.getStatus()) {
                    case SUCCESS:
                        List<Notification_UserSubCol> notifications = stateModel.getData();
                        mNotificationAdapter = new NotificationAdapter(notifications, NotificationsFragment.this);
                        mRvNotifications.setAdapter(mNotificationAdapter);

                        break;
                }
            }
        });
    }
}
