package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.ExpandableGroupHolder;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.ISwipeToDeleteHolder;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.TodoViewHolder;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.model.firebase.TodoList;
import vnu.uet.mobilecourse.assistant.view.calendar.TodoListsFragment;
import vnu.uet.mobilecourse.assistant.viewmodel.expandable.ExpandableTodoList;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public class TodoListAdapter extends
        ExpandableRecyclerViewAdapter<TodoListAdapter.TodoListViewHolder, TodoViewHolder> {

    private TodoListsFragment mOwner;
    private List<TodoList> mTodoLists;
    private LayoutInflater mInflater;
    private NavController mNavController;

    public TodoListAdapter(List<TodoList> todoLists, TodoListsFragment owner) {
        super(ExpandableTodoList.convert(todoLists));

        this.mOwner = owner;
        this.mInflater = owner.getLayoutInflater();
        this.mTodoLists = todoLists;

        Activity activity = owner.getActivity();
        if (activity != null) {
            mNavController = Navigation
                    .findNavController(activity, R.id.nav_host_fragment);
        }
    }

    @Override
    public TodoListViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater
                .inflate(R.layout.layout_expandable_parent_item, parent, false);

        return new TodoListViewHolder(view);
    }

    @Override
    public TodoViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater
                .inflate(R.layout.layout_todo_item, parent, false);

        return new TodoViewHolder(view, mOwner) {
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
    }

    @Override
    public void onBindChildViewHolder(TodoViewHolder holder, int flatPosition,
                                      ExpandableGroup group, int childIndex) {

        if (group instanceof ExpandableTodoList) {
            ExpandableTodoList content = (ExpandableTodoList) group;
            final Todo todo = content.getItems().get(childIndex);
            holder.bind(todo);
        }
    }

    @Override
    public void onBindGroupViewHolder(TodoListViewHolder holder, int flatPosition,
                                      ExpandableGroup group) {

        if (group instanceof ExpandableTodoList) {
            ExpandableTodoList content = (ExpandableTodoList) group;
            int index = expandableList.groups.indexOf(content);
            boolean expand = expandableList.expandedGroupIndexes[index];
            holder.bind(content, expand);
        }
    }

    public static class TodoListViewHolder extends ExpandableGroupHolder implements ISwipeToDeleteHolder {

        private TextView mTvTitle;

        private ExpandableTodoList mTodoList;

        TodoListViewHolder(@NonNull View itemView) {
            super(itemView);

            mTvTitle = itemView.findViewById(R.id.tvTitle);
        }

        void bind(ExpandableTodoList content, boolean expand) {
            bindArrow(expand);

            mTvTitle.setText(content.getTitle());

            mTodoList = content;
        }

        public ExpandableTodoList getTodoList() {
            return mTodoList;
        }
    }
}
