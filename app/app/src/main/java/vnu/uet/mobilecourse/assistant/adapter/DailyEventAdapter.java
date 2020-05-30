package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.TodoViewHolder;
import vnu.uet.mobilecourse.assistant.model.event.CourseSessionEvent;
import vnu.uet.mobilecourse.assistant.model.event.IEvent;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.model.event.DailyEventList;
import vnu.uet.mobilecourse.assistant.view.calendar.CalendarFragment;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public class DailyEventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
            return COURSE_SESSION_TYPE;

        return -1;
    }

    private static final int TYPE_TODO = 0;
    private static final int COURSE_SESSION_TYPE = 1;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mOwner.getLayoutInflater()
                .inflate(R.layout.layout_todo_item, parent, false);

        RecyclerView.ViewHolder holder = null;

        switch (viewType) {
            case TYPE_TODO:
                holder = new TodoViewHolder(view) {
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
        }

        Activity activity = mOwner.getActivity();

        if (activity != null) {
            mNavController = Navigation
                    .findNavController(activity, R.id.nav_host_fragment);
        }

        return holder;
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final IEvent event = mDailyList.get(position);

    }


    @Override
    public int getItemCount() {
        return mDailyList.size();
    }

    public DailyEventList getTodoList() {
        return mDailyList;
    }
}
