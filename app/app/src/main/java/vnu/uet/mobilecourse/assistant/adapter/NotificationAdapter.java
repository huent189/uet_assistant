package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.firebase.notification.Notification_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.model.firebase.notification.TodoNotification;
import vnu.uet.mobilecourse.assistant.repository.firebase.TodoRepository;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

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

    public class NotificationHolder extends RecyclerView.ViewHolder {

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

        private String generateTime(long seconds) {
            Date notifyTime = DateTimeUtils.fromSecond(seconds);
            String time = DateTimeUtils.DATE_TIME_FORMAT.format(notifyTime);

            long diff = Math.abs(System.currentTimeMillis() - notifyTime.getTime());
            // under 1 minute
            if (diff < 60 * 1000) {
                time = String.format(Locale.ROOT, "%d giây trước", diff / 1000);
            }
            // under 1 hour
            else if (diff < 60 * 60 * 1000) {
                time = String.format(Locale.ROOT, "%d phút trước", diff / 1000 / 60);
            }
            // under 1 day
            else if (diff < 24 * 60 * 60 * 1000) {
                time = String.format(Locale.ROOT, "%d giờ trước", diff / 1000 / 60 / 60);
            }

            return time;
        }

        private void navigateTodo(TodoNotification todoNotification) {
            String todoId = todoNotification.getTodoId();

            TodoRepository.getInstance().getTodoById(todoId)
                .observe(mOwner.getViewLifecycleOwner(), new Observer<StateModel<Todo>>() {
                    @Override
                    public void onChanged(StateModel<Todo> stateModel) {
                        switch (stateModel.getStatus()) {
                            case SUCCESS:
                                Todo todo = stateModel.getData();

                                todoNotification.setTodo(todo);

                                Bundle bundle = new Bundle();
                                bundle.putParcelable("todo", todo);

                                int actionId = R.id.action_navigation_notifications_to_navigation_modify_todo;
                                mNavController.navigate(actionId, bundle);

                                break;

                            case ERROR:
                                Context context = mOwner.getContext();
                                final String TODO_NOT_FOUND_MSG = "Công việc không tồn tại";

                                Toast.makeText(context, TODO_NOT_FOUND_MSG, Toast.LENGTH_SHORT).show();

                                break;
                        }
                    }
                });
        }

        public void bind(Notification_UserSubCol notification) {
            String title = notification.getTitle();
            mTvNotifyTitle.setText(title);

            String desc = notification.getDescription();
            mTvNotifyDesc.setText(desc);

            String time = generateTime(notification.getNotifyTime());
            mTvNotifyTime.setText(time);

            if (notification instanceof TodoNotification) {
                TodoNotification todoNotification = (TodoNotification) notification;

                mBtnViewNotify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        navigateTodo(todoNotification);
                    }
                });

            }
        }
    }
}
