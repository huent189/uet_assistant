package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.FirebaseModel.TodoDocument;
import vnu.uet.mobilecourse.assistant.model.todo.DailyTodoList;
import vnu.uet.mobilecourse.assistant.model.todo.Todo;
import vnu.uet.mobilecourse.assistant.repository.TodoRepository;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = owner.getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.layout_todo_item, parent, false);

        ViewHolder holder = new ViewHolder(view);

        Activity activity = owner.getActivity();

        if (activity != null)
            navController = Navigation.findNavController(activity, R.id.nav_host_fragment);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "call onBindViewHolder");

        final TodoDocument todo = todoList.get(position);

        holder.tvTodoTitle.setText(todo.getTitle());
        holder.tvDeadline.setText(DateTimeUtils.DATE_TIME_FORMAT.format(todo.getDeadline()));

        String todoListTitle = TodoRepository.getInstance()
                .getAllTodoLists()
                .getValue().getData()
                .stream()
                .filter(todoList -> todoList.getTodoListId().equals(todo.getTodoListId()))
                .findFirst()
                .orElse(null)
                .getTitle();

        holder.tvCategory.setText(todoListTitle);

        holder.cbDone.setActivated(todo.getStatus().equals(TodoDocument.DONE));
    }


    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTodoTitle;
        private TextView tvCategory;
        private TextView tvDeadline;
        private CheckBox cbDone;

        public ViewHolder(@NonNull View view) {
            super(view);

            tvTodoTitle = view.findViewById(R.id.tvTodoTitle);
            tvCategory = view.findViewById(R.id.tvCategory);
            tvDeadline = view.findViewById(R.id.tvDeadline);
            cbDone = view.findViewById(R.id.cbDone);
        }
    }
}
