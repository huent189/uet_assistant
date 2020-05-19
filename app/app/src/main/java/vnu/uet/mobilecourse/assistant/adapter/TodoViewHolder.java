package vnu.uet.mobilecourse.assistant.adapter;

import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.FirebaseModel.Todo;
import vnu.uet.mobilecourse.assistant.model.FirebaseModel.TodoList;
import vnu.uet.mobilecourse.assistant.repository.TodoRepository;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public abstract class TodoViewHolder extends ChildViewHolder {
    private ImageView ivAlarm;

    private TextView tvDeadline;

    private TextView tvTodoTitle;

    private CheckBox cbDone;

    private TextView tvCategory;

    private StrikethroughSpan strikethroughSpan = new StrikethroughSpan();

    private SpannableString text;

    public TodoViewHolder(@NonNull View itemView) {
        super(itemView);

        tvTodoTitle = itemView.findViewById(R.id.tvTodoTitle);
        ivAlarm = itemView.findViewById(R.id.ivAlarm);
        tvDeadline = itemView.findViewById(R.id.tvDeadline);
        cbDone = itemView.findViewById(R.id.cbDone);
        tvCategory = itemView.findViewById(R.id.tvCategory);
    }

    public void bind(Todo todo, boolean showList, LifecycleOwner lifecycleOwner) {
        String title = todo.getTitle();
        text = new SpannableString(title);
        tvTodoTitle.setText(text);

        Date deadline = DateTimeUtils.fromSecond(todo.getDeadline());
        tvDeadline.setText(DateTimeUtils.DATE_TIME_FORMAT.format(deadline));

        if (showList) {
            TodoRepository.getInstance().getShallowTodoLists()
                    .observe(lifecycleOwner, new Observer<StateModel<List<TodoList>>>() {
                        @Override
                        public void onChanged(StateModel<List<TodoList>> stateModel) {
                            String todoListTitle = "Đang tải";

                            switch (stateModel.getStatus()) {
                                case SUCCESS:
                                    TodoList todoList = stateModel.getData().stream()
                                            .filter(Objects::nonNull)
                                            .filter(list -> list.getId().equals(todo.getTodoListId()))
                                            .findFirst()
                                            .orElse(null);

                                    if (todoList != null)
                                        todoListTitle = todoList.getTitle();

                                    tvCategory.setText(todoListTitle);
                            }
                        }
                    });
        }

        if (todo.isCompleted()) {
            updateDoneEffect(title);
        }

        cbDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDoneEffect(title);

                    onMarkAsDone(todo).observe(lifecycleOwner, new Observer<StateModel<String>>() {
                        @Override
                        public void onChanged(StateModel<String> state) {
                            switch (state.getStatus()) {
                                case ERROR:
                                    updateDoingEffect();
                                    break;
                            }
                        }
                    });

                } else {
                    updateDoingEffect();

                    onMarkAsDoing(todo).observe(lifecycleOwner, new Observer<StateModel<String>>() {
                        @Override
                        public void onChanged(StateModel<String> state) {
                            switch (state.getStatus()) {
                                case ERROR:
                                    updateDoneEffect(title);
                                    break;
                            }
                        }
                    });
                }
            }
        });

        tvCategory.setVisibility(showList ? View.VISIBLE : View.GONE);
    }

    private void updateDoneEffect(String title) {
        text.setSpan(strikethroughSpan, 0, title.length() - 1, 0);
        tvTodoTitle.setText(text);

        cbDone.setChecked(true);
    }

    private void updateDoingEffect() {
        text.removeSpan(strikethroughSpan);
        tvTodoTitle.setText(text);

        cbDone.setChecked(false);
    }

    protected abstract IStateLiveData<String> onMarkAsDone(Todo todo);

    protected abstract IStateLiveData<String> onMarkAsDoing(Todo todo);
}