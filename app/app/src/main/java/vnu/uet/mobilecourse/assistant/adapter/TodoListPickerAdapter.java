package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.Course;
import vnu.uet.mobilecourse.assistant.model.todo.Todo;
import vnu.uet.mobilecourse.assistant.model.todo.TodoList;
import vnu.uet.mobilecourse.assistant.view.course.CoursesFragment;

public class TodoListPickerAdapter extends RecyclerView.Adapter<TodoListPickerAdapter.ViewHolder> {
    private static final String TAG = TodoListPickerAdapter.class.getSimpleName();

    private List<TodoList> todoLists;

    private Fragment owner;

    private NavController navController;

    private OnCheckedListener onCheckedListener;

    public TodoListPickerAdapter(List<TodoList> todoLists, Fragment owner) {
        this.todoLists = todoLists;
        this.owner = owner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = owner.getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.layout_select_todo_list_item, parent, false);

        ViewHolder holder = new ViewHolder(view);

        Activity activity = owner.getActivity();

        if (activity != null)
            navController = Navigation.findNavController(activity, R.id.nav_host_fragment);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "call onBindViewHolder");

        if (position == todoLists.size()) {
            holder.radioButton.setText("Thêm danh sách mới");

            holder.layoutContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        } else {
            final TodoList todoList = todoLists.get(position);

            String title = todoList.getTitle();

            holder.radioButton.setText(title);

            holder.radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        onCheckedListener.onChecked(todoList);
                    }
                }
            });
        }
    }

    public void setOnCheckedListener(OnCheckedListener listener) {
        this.onCheckedListener = listener;
    }

    public interface OnCheckedListener {
        void onChecked(TodoList todoList);
    }

    @Override
    public int getItemCount() {
        return todoLists.size() + 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RadioButton radioButton;
        private LinearLayout layoutContainer;

        public ViewHolder(@NonNull View view) {
            super(view);

            radioButton = view.findViewById(R.id.radioButton);
            layoutContainer = view.findViewById(R.id.layoutContainer);
        }
    }
}
