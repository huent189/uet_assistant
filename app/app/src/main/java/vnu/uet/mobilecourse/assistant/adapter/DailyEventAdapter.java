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

    private DailyTodoList mTodoList;
    private CalendarFragment mOwner;
    private NavController mNavController;

    public DailyEventAdapter(DailyTodoList todoList, CalendarFragment owner) {
        this.mTodoList = todoList;
        this.mOwner = owner;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mOwner.getLayoutInflater()
                .inflate(R.layout.layout_todo_item, parent, false);

        TodoViewHolder holder = new TodoViewHolder(view) {
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

        Activity activity = mOwner.getActivity();

        if (activity != null)
            mNavController = Navigation.findNavController(activity, R.id.nav_host_fragment);

        return holder;
    }



    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        final Todo todo = mTodoList.get(position);
        holder.bind(todo, true, mOwner.getViewLifecycleOwner());
    }


    @Override
    public int getItemCount() {
        return mTodoList.size();
    }

    public DailyTodoList getTodoList() {
        return mTodoList;
    }
}
