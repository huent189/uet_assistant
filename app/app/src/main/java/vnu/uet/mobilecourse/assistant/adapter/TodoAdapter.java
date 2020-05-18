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

        String title = todo.getTitle();
        SpannableString text = new SpannableString(title);
        holder.tvTodoTitle.setText(text);

        Date deadline = DateTimeUtils.fromSecond(todo.getDeadline());
        holder.tvDeadline.setText(DateTimeUtils.DATE_TIME_FORMAT.format(deadline));

        TodoRepository.getInstance().getShallowTodoLists()
                .observe(owner.getViewLifecycleOwner(), new Observer<StateModel<List<TodoListDocument>>>() {
                    @Override
                    public void onChanged(StateModel<List<TodoListDocument>> stateModel) {
                        String todoListTitle = "Đang tải";

                        switch (stateModel.getStatus()) {
                            case SUCCESS:
                                TodoListDocument todoList = stateModel.getData().stream()
                                        .filter(list -> list.getTodoListId().equals(todo.getTodoListId()))
                                        .findFirst()
                                        .orElse(null);

                                if (todoList != null)
                                    todoListTitle = todoList.getTitle();

                                holder.tvCategory.setText(todoListTitle);
                        }
                    }
                });

        StrikethroughSpan strikethroughSpan = new StrikethroughSpan();

        holder.cbDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    text.setSpan(strikethroughSpan, 0, title.length() - 1, 0);
                    holder.tvTodoTitle.setText(text);
                    todo.setStatus(TodoDocument.DONE);

                } else {
                    text.removeSpan(strikethroughSpan);
                    holder.tvTodoTitle.setText(text);
                    todo.setStatus(TodoDocument.DOING);
                }
            }
        });

        String status = todo.getStatus();
        if (status != null && status.equals(TodoDocument.DONE))
            holder.cbDone.setActivated(true);
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
