package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.FirebaseModel.TodoDocument;
import vnu.uet.mobilecourse.assistant.model.FirebaseModel.TodoListDocument;
import vnu.uet.mobilecourse.assistant.model.todo.ExpandableTodoList;
import vnu.uet.mobilecourse.assistant.model.todo.Todo;
import vnu.uet.mobilecourse.assistant.model.todo.TodoList;
import vnu.uet.mobilecourse.assistant.repository.TodoRepository;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;

public class TodoListAdapter extends
        ExpandableRecyclerViewAdapter<TodoListAdapter.TodoListViewHolder, TodoListAdapter.TodoViewHolder> {

    private Fragment owner;

    private List<TodoListDocument> todoLists;

    private LayoutInflater inflater;

    private NavController navController;

    public TodoListAdapter(List<TodoListDocument> todoLists, Fragment owner) {
        super(ExpandableTodoList.convert(todoLists));

        this.owner = owner;
        this.inflater = owner.getLayoutInflater();

        Activity activity = owner.getActivity();

        if (activity != null)
            navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
    }

    @Override
    public TodoListViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_expandable_parent_item, parent, false);
        return new TodoListViewHolder(view);
    }

    @Override
    public TodoViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_todo_item, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(TodoViewHolder holder, int flatPosition,
                                      ExpandableGroup group, int childIndex) {

        if (group instanceof ExpandableTodoList) {
            ExpandableTodoList content = (ExpandableTodoList) group;
            final TodoDocument todo = content.getItems().get(childIndex);
            holder.bind(todo);
        }
    }

    @Override
    public void onBindGroupViewHolder(TodoListViewHolder holder, int flatPosition,
                                      ExpandableGroup group) {

        if (group instanceof ExpandableTodoList) {
            ExpandableTodoList content = (ExpandableTodoList) group;
            holder.bind(content);
        }
    }

    public class TodoListViewHolder extends GroupViewHolder {
        private TextView tvTitle;

        private ImageView ivExpandArrow;

        public TodoListViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivExpandArrow = itemView.findViewById(R.id.ivExpandArrow);
        }

        public void bind(ExpandableTodoList content) {
            tvTitle.setText(content.getTitle());
        }
    }


    public class TodoViewHolder extends ChildViewHolder {
        private ImageView ivAlarm;

        private TextView tvDeadline;

        private TextView tvTodoTitle;

        private CheckBox cbDone;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTodoTitle = itemView.findViewById(R.id.tvTodoTitle);
            ivAlarm = itemView.findViewById(R.id.ivAlarm);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            cbDone = itemView.findViewById(R.id.cbDone);

            TextView tvCategory = itemView.findViewById(R.id.tvCategory);
            tvCategory.setVisibility(View.GONE);
        }

        public void bind(TodoDocument todo) {
            String title = todo.getTitle();
            tvTodoTitle.setText(title);

            cbDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // TODO: update on firebase

                    if (isChecked) {
                        todo.setStatus(TodoDocument.DONE);

                    } else {
                        todo.setStatus(TodoDocument.DOING);

                    }
                }
            });

            String status = todo.getStatus();

            if (status != null && status.equals(TodoDocument.DONE)) {
                // strike through todoTitle
                SpannableString text = new SpannableString(title);
                text.setSpan(new StrikethroughSpan(), 0, title.length() - 1, 0);
                tvTodoTitle.setText(text);
                cbDone.setActivated(true);

            } else {
                cbDone.setActivated(false);
            }

            Date deadline = DateTimeUtils.fromSecond(todo.getDeadline());
            tvDeadline.setText(DateTimeUtils.DATE_TIME_FORMAT.format(deadline));
        }
    }
}
