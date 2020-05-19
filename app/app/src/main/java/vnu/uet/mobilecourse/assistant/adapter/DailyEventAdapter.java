package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.model.todo.DailyTodoList;
import vnu.uet.mobilecourse.assistant.view.calendar.CalendarFragment;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public class DailyEventAdapter extends RecyclerView.Adapter<TodoViewHolder> {
    private static final String TAG = DailyEventAdapter.class.getSimpleName();

    private DailyTodoList todoList;

    private CalendarFragment owner;

    private NavController navController;

    public DailyEventAdapter(DailyTodoList todoList, CalendarFragment owner) {
        this.todoList = todoList;
        this.owner = owner;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = owner.getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.layout_todo_item, parent, false);

        TodoViewHolder holder = new TodoViewHolder(view) {
            @Override
            protected IStateLiveData<String> onMarkAsDone(Todo todo) {
                return owner.getViewModel().markTodoAsDone(todo.getId());
            }

            @Override
            protected IStateLiveData<String> onMarkAsDoing(Todo todo) {
                return owner.getViewModel().markTodoAsDoing(todo.getId());
            }
        };

        Activity activity = owner.getActivity();

        if (activity != null)
            navController = Navigation.findNavController(activity, R.id.nav_host_fragment);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        Log.d(TAG, "call onBindViewHolder");

        final Todo todo = todoList.get(position);

        holder.bind(todo, true, owner.getViewLifecycleOwner());
    }


    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public DailyTodoList getTodoList() {
        return todoList;
    }
}
