package vnu.uet.mobilecourse.assistant.view.calendar;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.todo.TodoList;
import vnu.uet.mobilecourse.assistant.viewmodel.CalendarSharedViewModel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.Random;

import static android.app.Activity.RESULT_OK;

public class AddTodoListFragment extends Fragment {

    private CalendarSharedViewModel mViewModel;

    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        FragmentActivity activity = getActivity();
        if (activity != null) {
            mViewModel = new ViewModelProvider(activity).get(CalendarSharedViewModel.class);
            navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
        }

        View root = inflater.inflate(R.layout.fragment_add_todo_list, container, false);

        EditText etTitle = root.findViewById(R.id.etTitle);
        EditText etDescription = root.findViewById(R.id.etDescription);

        Button btnSave = root.findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TodoList todoList = new TodoList();

                String title = etTitle.getText().toString();
                String desc = etDescription.getText().toString();
                todoList.setTitle(title);
                todoList.setDescription(desc);

                String id = String.valueOf(new Random().nextInt(10000));
                todoList.setId(id);

                mViewModel.getTodoListTitle().postValue(todoList.getTitle());

                mViewModel.addTodoList(todoList);

                navController.navigateUp();
            }
        });

        return root;
    }
}