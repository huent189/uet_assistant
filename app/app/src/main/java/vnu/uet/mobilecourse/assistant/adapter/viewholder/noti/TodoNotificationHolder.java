package vnu.uet.mobilecourse.assistant.adapter.viewholder.noti;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.model.notification.TodoNotification;
import vnu.uet.mobilecourse.assistant.repository.firebase.TodoRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class TodoNotificationHolder extends NotificationHolder<TodoNotification> {

    public TodoNotificationHolder(@NonNull View view) {
        super(view);
    }

    @Override
    protected int getIconResId() {
        return R.drawable.img_target;
    }

    @Override
    protected void onNavigate(TodoNotification notification, NavController navController, Fragment owner) {
        String todoId = notification.getTodoId();

        TodoRepository.getInstance().getTodoById(todoId)
                .observe(owner.getViewLifecycleOwner(), new Observer<StateModel<Todo>>() {
                    @Override
                    public void onChanged(StateModel<Todo> stateModel) {
                        switch (stateModel.getStatus()) {
                            case SUCCESS:
                                Todo todo = stateModel.getData();

                                notification.setTodo(todo);

                                Bundle bundle = new Bundle();
                                bundle.putParcelable("todo", todo);

                                int actionId = R.id.action_navigation_notifications_to_navigation_modify_todo;
                                navController.navigate(actionId, bundle);

                                break;

                            case ERROR:
                                Context context = owner.getContext();
                                final String TODO_NOT_FOUND_MSG = "Công việc không tồn tại";

                                Toast.makeText(context, TODO_NOT_FOUND_MSG, Toast.LENGTH_SHORT).show();

                                break;
                        }
                    }
                });
    }
}
