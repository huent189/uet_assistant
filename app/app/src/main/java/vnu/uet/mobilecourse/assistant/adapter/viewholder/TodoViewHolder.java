package vnu.uet.mobilecourse.assistant.adapter.viewholder;

import android.view.View;

import java.util.Date;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import vnu.uet.mobilecourse.assistant.model.event.IEvent;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.view.calendar.CalendarFragment;
import vnu.uet.mobilecourse.assistant.view.calendar.TodoListsFragment;
import vnu.uet.mobilecourse.assistant.viewmodel.CalendarViewModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public abstract class TodoViewHolder extends EventViewHolder implements ISwipeToDeleteHolder {

    private boolean mShowList;
    private LifecycleOwner mLifecycleOwner;
    private CalendarViewModel mViewModel;
    private Todo mTodo;

    private TodoViewHolder(@NonNull View itemView, boolean showList) {
        super(itemView);

        mShowList = showList;
    }

    protected TodoViewHolder(@NonNull View itemView, @NonNull TodoListsFragment owner) {
        this(itemView, false);

        mLifecycleOwner = owner.getViewLifecycleOwner();
        mViewModel = owner.getViewModel();
    }

    protected TodoViewHolder(@NonNull View itemView, @NonNull CalendarFragment owner) {
        this(itemView, true);

        mLifecycleOwner = owner.getViewLifecycleOwner();
        mViewModel = owner.getViewModel();
    }

    public Todo getTodo() {
        return mTodo;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        itemView.setOnClickListener(listener);
    }

    @Override
    public void bind(IEvent event) {
        super.bind(event);

        if (event instanceof Todo) {
            Todo todo = (Todo) event;
            mTodo = todo;

            if (!mShowList) {
                // setup deadline text
                Date deadline = DateTimeUtils.fromSecond(todo.getDeadline());
                mTvDeadline.setText(DateTimeUtils.DATE_TIME_FORMAT.format(deadline));

                // hide category text
                mTvCategory.setVisibility(View.GONE);
            } else {
                mViewModel.getShallowTodoLists()
                        .observe(mLifecycleOwner, stateModel -> {
                            if (stateModel.getStatus() == StateStatus.SUCCESS) {
                                stateModel.getData().stream()
                                        .filter(Objects::nonNull)
                                        .filter(list -> list.getId().equals(todo.getTodoListId()))
                                        .findFirst()
                                        .ifPresent(todoList -> mTvCategory.setText(todoList.getTitle()));
                            }
                        });
            }

            mCbDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // mark as done
                if (isChecked) {
                    updateDoneEffect();

                    onMarkAsDone(todo).observe(mLifecycleOwner, state -> {
                        // recover in case catch a error
                        if (state.getStatus() == StateStatus.ERROR) {
                            updateDoingEffect(todo.getDeadline());
                        }
                    });

                }
                // mark as doing
                else {
                    updateDoingEffect(todo.getDeadline());

                    onMarkAsDoing(todo).observe(mLifecycleOwner, state -> {
                        // recover in case catch a error
                        if (state.getStatus() == StateStatus.ERROR) {
                            updateDoneEffect();
                        }
                    });
                }
            });
        }
    }

    protected abstract IStateLiveData<String> onMarkAsDone(Todo todo);

    protected abstract IStateLiveData<String> onMarkAsDoing(Todo todo);
}