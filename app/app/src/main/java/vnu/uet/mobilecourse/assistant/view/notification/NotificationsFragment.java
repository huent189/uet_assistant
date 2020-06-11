package vnu.uet.mobilecourse.assistant.view.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.NotificationAdapter;
import vnu.uet.mobilecourse.assistant.model.firebase.notification.Notification_UserSubCol;
import vnu.uet.mobilecourse.assistant.view.component.SwipeToDeleteCallback;
import vnu.uet.mobilecourse.assistant.viewmodel.NotificationsViewModel;

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
        mRvNotifications.setLayoutManager(new LinearLayoutManager(this.getContext()));

        ImageView ivEmpty = root.findViewById(R.id.ivEmpty);

        ShimmerFrameLayout sflNotifications = root.findViewById(R.id.sflNotifications);
        sflNotifications.startShimmerAnimation();

        mViewModel.getNotifications().observe(getViewLifecycleOwner(), stateModel -> {
            switch (stateModel.getStatus()) {
                case LOADING:
                    sflNotifications.setVisibility(View.VISIBLE);
                    ivEmpty.setVisibility(View.GONE);
                    mRvNotifications.setVisibility(View.GONE);
                    break;

                case SUCCESS:
                    List<Notification_UserSubCol> notifications = stateModel.getData();

                    if (notifications.isEmpty()) {
                        ivEmpty.setVisibility(View.VISIBLE);
                        mRvNotifications.setVisibility(View.GONE);
                    } else {
                        mNotificationAdapter = new NotificationAdapter(notifications, NotificationsFragment.this);
                        mRvNotifications.setAdapter(mNotificationAdapter);

                        ivEmpty.setVisibility(View.GONE);
                        mRvNotifications.setVisibility(View.VISIBLE);
                    }

                    sflNotifications.setVisibility(View.GONE);

                    break;
            }
        });
    }
}
