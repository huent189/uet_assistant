package vnu.uet.mobilecourse.assistant.view.calendar;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.util.Util;

import java.text.ParseException;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.alarm.receiver.TodoReceiver;
import vnu.uet.mobilecourse.assistant.alarm.scheduler.TodoScheduler;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;

public class AddTodoFragment extends TodoFragment {

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_add_todo;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = super.onCreateView(inflater, container, savedInstanceState);

        mBtnSave.setOnClickListener(v -> save());

        return root;
    }

    @Override
    protected int getCreateTodoListActionId() {
        return R.id.action_navigation_add_todo_to_navigation_add_todo_list;
    }

    private void save() {
        if (!validate())
            return;

        try {
            Todo todo = generateTodo();
            mViewModel.addTodo(todo).observe(getViewLifecycleOwner(), stateModel -> {
                switch (stateModel.getStatus()) {
                    case ERROR:
                        showFailureToast(stateModel.getError());
                        break;

                    case LOADING:
                        Toast.makeText(getContext(),"Tạo thành công", Toast.LENGTH_SHORT).show();
                        TodoScheduler.getInstance(getContext()).schedule(todo);

//                        Context mContext = getContext().getApplicationContext();
//
//                        Intent intent = new Intent(mContext, TodoReceiver.class);
//                        intent.setAction(TodoScheduler.ACTION);
//                        intent.putExtra("???", "???");
//                        PendingIntent pendingIntent = PendingIntent
//                                .getBroadcast(mContext, 0, intent, 0);
//
//                        AlarmManager mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
//                        long time = todo.getDeadline() * 1000;
//                        mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);

//                        TodoHandler.getInstance().schedule(getContext(), todo);
                        mNavController.navigateUp();
                }
            });
        }
        catch (ParseException e) {
            showFailureToast(e);
        }
    }

    private Todo generateTodo() throws ParseException {
        Todo todo = new Todo();

        @SuppressLint("RestrictedApi")
        String id = Util.autoId();
        todo.setId(id);

        String ownerId = User.getInstance().getStudentId();
        todo.setOwnerId(ownerId);

        String title = mEtTodoTitle.getText().toString();
        todo.setTitle(title);

        String desc = mEtDescription.getText().toString();
        todo.setDescription(desc);

        String todoListId = mViewModel.getShallowTodoLists()
                .getValue().getData()
                .get(mRgTodoList.getCheckedRadioButtonId())
                .getId();

        todo.setTodoListId(todoListId);

        String date = mBtnDate.getText().toString();
        String time = mBtnTime.getText().toString();

        Date deadlineDate = DateTimeUtils.DATE_TIME_FORMAT.parse(date + " " + time);
        assert deadlineDate != null;
        int deadline = (int) (deadlineDate.getTime() / 1000);
        todo.setDeadline(deadline);

        return todo;
    }

}
