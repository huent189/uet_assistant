package vnu.uet.mobilecourse.assistant.adapter;

import android.graphics.Color;
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
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.model.firebase.TodoList;
import vnu.uet.mobilecourse.assistant.repository.firebase.TodoRepository;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public abstract class TodoViewHolder extends ChildViewHolder {

    private ImageView mIvAlarm;
    private TextView mTvDeadline;
    private TextView mTvTodoTitle;
    private TextView mTvCategory;
    private CheckBox mCbDone;
    private StrikethroughSpan mStrikeSpan = new StrikethroughSpan();
    private SpannableString mTitleText;
    private TextView mLayoutDisable;

    TodoViewHolder(@NonNull View itemView) {
        super(itemView);

        mTvTodoTitle = itemView.findViewById(R.id.tvTodoTitle);
        mIvAlarm = itemView.findViewById(R.id.ivAlarm);
        mTvDeadline = itemView.findViewById(R.id.tvDeadline);
        mCbDone = itemView.findViewById(R.id.cbDone);
        mTvCategory = itemView.findViewById(R.id.tvCategory);
        mLayoutDisable = itemView.findViewById(R.id.layout_disable);
    }

    void bind(Todo todo, boolean showList, LifecycleOwner lifecycleOwner) {
        // setup title text
        String title = todo.getTitle();
        mTitleText = new SpannableString(title);
        mTvTodoTitle.setText(mTitleText);

        // setup deadline text
        Date deadline = DateTimeUtils.fromSecond(todo.getDeadline());
        mTvDeadline.setText(DateTimeUtils.DATE_TIME_FORMAT.format(deadline));

        // show category case
        if (showList) {
            TodoRepository.getInstance()
                    .getShallowTodoLists()
                    .observe(lifecycleOwner, stateModel -> {
                        String todoListTitle = LOADING_TITLE;

                        switch (stateModel.getStatus()) {
                            case SUCCESS:
                                TodoList todoList = stateModel.getData().stream()
                                        .filter(Objects::nonNull)
                                        .filter(list -> list.getId().equals(todo.getTodoListId()))
                                        .findFirst()
                                        .orElse(null);

                                if (todoList != null)
                                    todoListTitle = todoList.getTitle();

                                mTvCategory.setText(todoListTitle);
                        }
                    });
        }

        if (todo.isCompleted()) {
            updateDoneEffect(title);
        } else {
            updateDoingEffect(todo.getDeadline());
        }

        mCbDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // mark as done
            if (isChecked) {
                updateDoneEffect(title);

                onMarkAsDone(todo).observe(lifecycleOwner, state -> {
                    // recover in case catch a error
                    switch (state.getStatus()) {
                        case ERROR:
                            updateDoingEffect(todo.getDeadline());
                            break;
                    }
                });

            }
            // mark as doing
            else {
                updateDoingEffect(todo.getDeadline());

                onMarkAsDoing(todo).observe(lifecycleOwner, state -> {
                    // recover in case catch a error
                    switch (state.getStatus()) {
                        case ERROR:
                            updateDoneEffect(title);
                            break;
                    }
                });
            }
        });

        mTvCategory.setVisibility(showList ? View.VISIBLE : View.GONE);
    }

    private void updateDoneEffect(String title) {
        mTitleText.setSpan(mStrikeSpan, 0, title.length() - 1, 0);
        mTvTodoTitle.setText(mTitleText);

        mLayoutDisable.setVisibility(View.VISIBLE);

        mTvDeadline.setTextColor(Color.WHITE);
        mIvAlarm.setColorFilter(Color.WHITE);

        mCbDone.setChecked(true);
    }

    private void updateDoingEffect(long deadline) {
        mTitleText.removeSpan(mStrikeSpan);
        mTvTodoTitle.setText(mTitleText);

        mLayoutDisable.setVisibility(View.GONE);

        mCbDone.setChecked(false);

        if (deadline - System.currentTimeMillis() < WARNING_BOUNDARY) {
            mTvDeadline.setTextColor(RED_COLOR);
            mIvAlarm.setColorFilter(RED_COLOR);
        }
    }

    protected abstract IStateLiveData<String> onMarkAsDone(Todo todo);

    protected abstract IStateLiveData<String> onMarkAsDoing(Todo todo);

    private static final int WARNING_BOUNDARY = 60 * 60 * 1000; // 1 hour

    private static final int RED_COLOR = Color.parseColor("#FFF44336");

    private static final String LOADING_TITLE = "Đang tải";
}