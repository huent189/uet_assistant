package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.FirebaseModel.TodoDocument;
import vnu.uet.mobilecourse.assistant.model.FirebaseModel.TodoListDocument;
import vnu.uet.mobilecourse.assistant.model.todo.DailyTodoList;
import vnu.uet.mobilecourse.assistant.model.todo.Todo;
import vnu.uet.mobilecourse.assistant.repository.TodoRepository;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class TodoAdapter extends RecyclerView.Adapter<TodoViewHolder> {
    private static final String TAG = TodoAdapter.class.getSimpleName();

    private DailyTodoList todoList;

    private Fragment owner;

    private NavController navController;

    public TodoAdapter(DailyTodoList todoList, Fragment owner) {
        this.todoList = todoList;
        this.owner = owner;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = owner.getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.layout_todo_item, parent, false);

        TodoViewHolder holder = new TodoViewHolder(view);

        Activity activity = owner.getActivity();

        if (activity != null)
            navController = Navigation.findNavController(activity, R.id.nav_host_fragment);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        Log.d(TAG, "call onBindViewHolder");

        final TodoDocument todo = todoList.get(position);

        holder.bind(todo, true, owner.getViewLifecycleOwner());
    }


    @Override
    public int getItemCount() {
        return todoList.size();
    }
}
