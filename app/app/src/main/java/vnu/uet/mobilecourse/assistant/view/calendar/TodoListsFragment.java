package vnu.uet.mobilecourse.assistant.view.calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.TodoListAdapter;
import vnu.uet.mobilecourse.assistant.model.firebase.TodoList;
import vnu.uet.mobilecourse.assistant.repository.firebase.TodoRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.CalendarViewModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class TodoListsFragment extends Fragment {

    private CalendarViewModel viewModel;

    private FragmentActivity activity;

    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        activity = getActivity();

        assert activity != null;
        viewModel = new ViewModelProvider(activity).get(CalendarViewModel.class);

        View root = inflater.inflate(R.layout.fragment_todo_lists, container, false);

        initializeToolbar(root);

        if (activity != null)
            navController = Navigation.findNavController(activity, R.id.nav_host_fragment);


        RecyclerView rvTodoLists = root.findViewById(R.id.rvTodoLists);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvTodoLists.setLayoutManager(layoutManager);

        TodoListsFragment thisFragment = this;

        TodoRepository.getInstance().getAllTodoLists().observe(getViewLifecycleOwner(), new Observer<StateModel<List<TodoList>>>() {
            @Override
            public void onChanged(StateModel<List<TodoList>> stateModel) {
                switch (stateModel.getStatus()) {
                    case SUCCESS:
                        List<TodoList> todoLists = stateModel.getData();
                        TodoListAdapter adapter = new TodoListAdapter(todoLists, thisFragment);
                        rvTodoLists.setAdapter(adapter);
                        break;
                }

            }
        });

        return root;
    }

    private void initializeToolbar(View root) {
        if (activity instanceof AppCompatActivity) {
            Toolbar toolbar = root.findViewById(R.id.toolbar);

            ((AppCompatActivity) activity).setSupportActionBar(toolbar);
//            ((AppCompatActivity) activity).getSupportActionBar().setDisplayShowTitleEnabled(true);

            setHasOptionsMenu(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.todo_lists_toolbar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.action_view_calendar) {
            navController.navigate(R.id.action_navigation_todo_lists_to_navigation_calendar);
        }

        return super.onOptionsItemSelected(item);
    }

    public CalendarViewModel getViewModel() {
        return viewModel;
    }
}
