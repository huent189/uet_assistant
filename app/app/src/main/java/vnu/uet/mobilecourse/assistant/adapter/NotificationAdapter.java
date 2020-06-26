package vnu.uet.mobilecourse.assistant.adapter;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.noti.AdminNotificationHolder;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.noti.CourseNotificationHolder;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.noti.ForumNotificationHolder;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.noti.MaterialNotificationHolder;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.noti.NotificationHolder;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.noti.SubmissionNotificationHolder;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.noti.TodoNotificationHolder;
import vnu.uet.mobilecourse.assistant.model.firebase.NotificationType;
import vnu.uet.mobilecourse.assistant.model.notification.Notification_UserSubCol;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationHolder> {

    private List<? extends Notification_UserSubCol> mNotifications;
    private Fragment mOwner;

    public NotificationAdapter(List<? extends Notification_UserSubCol> notifications, Fragment owner) {
        this.mNotifications = notifications;
        this.mOwner = owner;
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mOwner.getLayoutInflater()
                .inflate(R.layout.layout_notification_item, parent, false);

        NotificationHolder holder;

        switch (viewType) {
            case NotificationType.TODO:
                holder = new TodoNotificationHolder(view);
                break;

            case NotificationType.MATERIAL:
                holder = new MaterialNotificationHolder(view);
                break;

            case NotificationType.ATTENDANCE:
                holder = new CourseNotificationHolder(view);
                break;

            case NotificationType.FORUM:
                holder = new ForumNotificationHolder(view);
                break;

            case NotificationType.SUBMISSION:
                holder = new SubmissionNotificationHolder(view);
                break;

            default:
                holder = new AdminNotificationHolder(view);
                break;
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {
        final Notification_UserSubCol notification = mNotifications.get(position);
        holder.bind(notification, mOwner);
    }

    @Override
    public int getItemViewType(int position) {
        return mNotifications.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

//    static class NotificationHolder extends RecyclerView.ViewHolder {
//
//        private ImageView mIvNotifyIcon;
//        private TextView mTvNotifyTitle;
//        private TextView mTvNotifyDesc;
//        private TextView mTvNotifyTime;
//        private ImageButton mBtnViewNotify;
//
//        private Fragment mOwner;
//        private NavController mNavController;
//
//        public NotificationHolder(@NonNull View view) {
//            super(view);
//
//            mIvNotifyIcon = view.findViewById(R.id.ivNotifyIcon);
//            mTvNotifyTitle = view.findViewById(R.id.tvNotifyTitle);
//            mTvNotifyDesc = view.findViewById(R.id.tvNotifyDesc);
//            mTvNotifyTime = view.findViewById(R.id.tvNotifyTime);
//            mBtnViewNotify = view.findViewById(R.id.btnViewNotify);
//        }
//
//        private String generateTime(long seconds) {
//            Date notifyTime = DateTimeUtils.fromSecond(seconds);
//            String time = DateTimeUtils.DATE_TIME_FORMAT.format(notifyTime);
//
//            long diff = Math.abs(System.currentTimeMillis() - notifyTime.getTime());
//            // under 1 minute
//            if (diff < 60 * 1000) {
//                time = String.format(Locale.ROOT, "%d giây trước", diff / 1000);
//            }
//            // under 1 hour
//            else if (diff < 60 * 60 * 1000) {
//                time = String.format(Locale.ROOT, "%d phút trước", diff / 1000 / 60);
//            }
//            // under 1 day
//            else if (diff < 24 * 60 * 60 * 1000) {
//                time = String.format(Locale.ROOT, "%d giờ trước", diff / 1000 / 60 / 60);
//            }
//
//            return time;
//        }
//
//        private void navigateMaterial(NewMaterialNotification notification) {
//            int courseId = notification.getCourseId();
//            int materialId = notification.getMaterialId();
//
//            CourseRepository.getInstance()
//                    .getContent(courseId).observe(mOwner.getViewLifecycleOwner(), new Observer<List<CourseOverview>>() {
//                        @Override
//                        public void onChanged(List<CourseOverview> courseOverviews) {
//                            if (courseOverviews != null && !courseOverviews.isEmpty()) {
//                                Material foundMaterial = null;
//
//                                for (CourseOverview overview : courseOverviews) {
//                                    for (Material material : overview.getMaterials()) {
//                                        if (material.getId() == materialId) {
//                                            foundMaterial = material;
//                                            break;
//                                        }
//                                    }
//
//                                    if (foundMaterial != null) break;
//                                }
//
//                                if (foundMaterial != null) {
//                                    Bundle bundle = new Bundle();
//                                    bundle.putParcelable("material", foundMaterial);
//                                    mNavController.navigate(R.id.action_navigation_notifications_to_navigation_material, bundle);
//                                } else {
//                                    Context context = mOwner.getContext();
//                                    final String MATERIAL_NOT_FOUND_MSG = "Không tìm thấy tài liệu";
//
//                                    Toast.makeText(context, MATERIAL_NOT_FOUND_MSG, Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        }
//                    });
//        }
//
//        private void navigateCourse(CourseAttendantNotification notification) {
//            String courseCode = notification.getCourseCode();
//
//            CourseRepository.getInstance().getFullCourses().observe(mOwner.getViewLifecycleOwner(), new Observer<StateModel<List<ICourse>>>() {
//                @Override
//                public void onChanged(StateModel<List<ICourse>> stateModel) {
//                    switch (stateModel.getStatus()) {
//                        case SUCCESS:
//                            List<ICourse> courses = stateModel.getData();
//
//                            courses.stream()
//                                    .filter(course -> course.getCode().equals(courseCode))
//                                    .findFirst()
//                                    .ifPresent(course -> {
//                                        Bundle bundle = new Bundle();
//                                        bundle.putParcelable("course", course);
//
//                                        mNavController.navigate(R.id.action_navigation_notifications_to_navigation_explore_course, bundle);
//                                    });
//
//                            break;
//
//                        case ERROR:
//                            Context context = mOwner.getContext();
//                            final String COURSE_NOT_FOUND_MSG = "Không tìm thấy khóa học";
//
//                            Toast.makeText(context, COURSE_NOT_FOUND_MSG, Toast.LENGTH_SHORT).show();
//                            break;
//
//                    }
//                }
//            });
//        }
//
//        private void navigateTodo(TodoNotification todoNotification) {
//            String todoId = todoNotification.getTodoId();
//
//            TodoRepository.getInstance().getTodoById(todoId)
//                .observe(mOwner.getViewLifecycleOwner(), new Observer<StateModel<Todo>>() {
//                    @Override
//                    public void onChanged(StateModel<Todo> stateModel) {
//                        switch (stateModel.getStatus()) {
//                            case SUCCESS:
//                                Todo todo = stateModel.getData();
//
//                                todoNotification.setTodo(todo);
//
//                                Bundle bundle = new Bundle();
//                                bundle.putParcelable("todo", todo);
//
//                                int actionId = R.id.action_navigation_notifications_to_navigation_modify_todo;
//                                mNavController.navigate(actionId, bundle);
//
//                                break;
//
//                            case ERROR:
//                                Context context = mOwner.getContext();
//                                final String TODO_NOT_FOUND_MSG = "Công việc không tồn tại";
//
//                                Toast.makeText(context, TODO_NOT_FOUND_MSG, Toast.LENGTH_SHORT).show();
//
//                                break;
//                        }
//                    }
//                });
//        }
//
//        public void bind(Notification_UserSubCol notification, Fragment owner) {
//            mOwner = owner;
//
//            Activity activity = mOwner.getActivity();
//
//            if (activity != null) {
//                mNavController = Navigation
//                        .findNavController(activity, R.id.nav_host_fragment);
//            }
//
//            String title = notification.getTitle();
//            mTvNotifyTitle.setText(title);
//
//            String desc = notification.getDescription();
//            mTvNotifyDesc.setText(desc);
//
//            String time = generateTime(notification.getNotifyTime());
//            mTvNotifyTime.setText(time);
//
//            if (notification instanceof TodoNotification) {
//                mIvNotifyIcon.setImageResource(R.drawable.img_target);
//
//                TodoNotification todoNotification = (TodoNotification) notification;
//
//                mBtnViewNotify.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        navigateTodo(todoNotification);
//                    }
//                });
//            } else if (notification instanceof AdminNotification) {
//                mIvNotifyIcon.setImageResource(R.drawable.img_admin_bot);
//            } else if (notification instanceof CourseAttendantNotification) {
//                mIvNotifyIcon.setImageResource(R.drawable.img_bag_bell);
//
//                CourseAttendantNotification cast = (CourseAttendantNotification) notification;
//
//                mBtnViewNotify.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        navigateCourse(cast);
//                    }
//                });
//
//            } else if (notification instanceof NewMaterialNotification) {
//                mIvNotifyIcon.setImageResource(R.drawable.img_material);
//
//                NewMaterialNotification cast = (NewMaterialNotification) notification;
//
//                mBtnViewNotify.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        navigateMaterial(cast);
//                    }
//                });
//            }
//        }
//    }
}
