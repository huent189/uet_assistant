package vnu.uet.mobilecourse.assistant.view.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.alarm.scheduler.TodoScheduler;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public class ModifyTodoFragment extends TodoFragment {

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_modify_todo;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = super.onCreateView(inflater, container, savedInstanceState);

        if (getArguments() != null) {
            Todo todo = getArguments().getParcelable("todo");

            if (todo != null) {
                mViewModel.getTodoTitle().postValue(todo.getTitle());
                mViewModel.getTodoDesc().postValue(todo.getDescription());
                mViewModel.setTodoListId(todo.getTodoListId());

                Date deadline = DateTimeUtils.fromSecond(todo.getDeadline());
                mViewModel.getTodoDate().postValue(DateTimeUtils.SHORT_DATE_FORMAT.format(deadline));
                mViewModel.getTodoTime().postValue(DateTimeUtils.TIME_12H_FORMAT.format(deadline));

                mBtnSave.setOnClickListener(v -> save(todo));
            }
        }

        return root;
    }

    @Override
    protected int getCreateTodoListActionId() {
        return R.id.action_navigation_modify_todo_to_navigation_add_todo_list;
    }

    private void save(Todo todo) {
        try {
            updateTodo(todo).observe(getViewLifecycleOwner(), stateModel -> {
                switch (stateModel.getStatus()) {
                    case ERROR:
                        showFailureToast(stateModel.getError());
                        break;

                    case LOADING:
                        Toast.makeText(getContext(),"Lưu thành công", Toast.LENGTH_SHORT).show();
                        TodoScheduler.getInstance(getContext()).schedule(todo);
                        mNavController.navigateUp();
                }
            });

        } catch (ParseException e) {
            showFailureToast(e);
        }
    }

    private IStateLiveData<String> updateTodo(Todo todo) throws ParseException {
        Map<String, Object> changes = new HashMap<>();

        String title = mEtTodoTitle.getText().toString();
        changes.put("title", title);

        String description = mEtDescription.getText().toString();
        changes.put("description", description);

        String todoListTitle = mTvTodoListTitle.getText().toString();
        mViewModel.getShallowTodoLists()
                .getValue()
                .getData()
                .stream()
                .filter(todoList -> todoList.getTitle().equals(todoListTitle))
                .findFirst()
                .ifPresent(list -> changes.put("todoListId", list.getId()));

        String date = mBtnDate.getText().toString();
        String time = mBtnTime.getText().toString();

        Date deadlineDate = DateTimeUtils.DATE_TIME_FORMAT.parse(date + " " + time);
        assert deadlineDate != null;
        int deadline = (int) (deadlineDate.getTime() / 1000);
        changes.put("deadline", deadline);

        return mViewModel.updateTodo(todo.getId(), changes);
    }
}