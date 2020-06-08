package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.EventViewHolder;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.TodoViewHolder;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.UnmodifiedEventViewHolder;
import vnu.uet.mobilecourse.assistant.model.event.CourseSessionEvent;
import vnu.uet.mobilecourse.assistant.model.event.IEvent;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.model.event.DailyEventList;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.view.calendar.CalendarFragment;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public class DailyEventAdapter extends RecyclerView.Adapter<EventViewHolder> {

    private DailyEventList mDailyList;
    private CalendarFragment mOwner;
    private NavController mNavController;

    public DailyEventAdapter(DailyEventList todoList, CalendarFragment owner) {
        this.mDailyList = todoList;
        this.mOwner = owner;
    }

    @Override
    public int getItemViewType(int position) {
        IEvent event = mDailyList.get(position);

        if (event instanceof Todo)
            return TYPE_TODO;
        else if (event instanceof CourseSessionEvent)
            return TYPE_COURSE_SESSION;

        return -1;
    }

    private static final int TYPE_TODO = 0;
    private static final int TYPE_COURSE_SESSION = 1;

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mOwner.getLayoutInflater()
                .inflate(R.layout.layout_todo_item, parent, false);

        EventViewHolder holder;

        switch (viewType) {
            case TYPE_TODO:
                holder = new TodoViewHolder(view, mOwner) {
                    @Override
                    protected IStateLiveData<String> onMarkAsDone(Todo todo) {
                        mOwner.saveRecycleViewState();
                        return mOwner.getViewModel().markTodoAsDone(todo.getId());
                    }

                    @Override
                    protected IStateLiveData<String> onMarkAsDoing(Todo todo) {
                        mOwner.saveRecycleViewState();
                        return mOwner.getViewModel().markTodoAsDoing(todo.getId());
                    }
                };

                break;

            default:
                holder = new UnmodifiedEventViewHolder(view);
                break;
        }

        Activity activity = mOwner.getActivity();

        if (activity != null) {
            mNavController = Navigation
                    .findNavController(activity, R.id.nav_host_fragment);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        final IEvent event = mDailyList.get(position);

        holder.bind(event);

        if (holder instanceof TodoViewHolder && event instanceof Todo) {
            TodoViewHolder todoViewHolder = (TodoViewHolder) holder;
            Todo todo = (Todo) event;

            todoViewHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("todo", todo);

                    mNavController.navigate(R.id.action_navigation_calendar_to_navigation_modify_todo, bundle);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDailyList.size();
    }

    public DailyEventList getTodoList() {
        return mDailyList;
    }
}
