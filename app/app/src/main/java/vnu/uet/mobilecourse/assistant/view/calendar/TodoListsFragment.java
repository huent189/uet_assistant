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

    private CalendarViewModel mViewModel;
    private FragmentActivity mActivity;
    private NavController mNavController;
    private TodoListAdapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mActivity = getActivity();

        assert mActivity != null;
        mViewModel = new ViewModelProvider(mActivity).get(CalendarViewModel.class);

        View root = inflater.inflate(R.layout.fragment_todo_lists, container, false);

        initializeToolbar(root);

        if (mActivity != null) {
            mNavController = Navigation
                    .findNavController(mActivity, R.id.nav_host_fragment);
        }

        RecyclerView rvTodoLists = root.findViewById(R.id.rvTodoLists);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvTodoLists.setLayoutManager(layoutManager);

        TodoListsFragment thisFragment = this;

        // get bundle from prev fragment
        if (getArguments() == null) {
            setArguments(new Bundle());
        }
        Bundle args = getArguments();

        mViewModel.getAllTodoLists().observe(getViewLifecycleOwner(), stateModel -> {
            switch (stateModel.getStatus()) {
                case SUCCESS:
                    List<TodoList> todoLists = stateModel.getData();
                    mAdapter = new TodoListAdapter(todoLists, thisFragment);
                    rvTodoLists.setAdapter(mAdapter);

                    mAdapter.onRestoreInstanceState(args);
                    break;

                case LOADING:
                case ERROR:
                    if (mAdapter != null) {
                        mAdapter.onSaveInstanceState(args);
                    }

                    break;
            }
        });

        return root;
    }

    private void initializeToolbar(View root) {
        if (mActivity instanceof AppCompatActivity) {
            Toolbar toolbar = root.findViewById(R.id.toolbar);
            ((AppCompatActivity) mActivity).setSupportActionBar(toolbar);
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
            mNavController.navigate(R.id.action_navigation_todo_lists_to_navigation_calendar);
        }

        return super.onOptionsItemSelected(item);
    }

    public CalendarViewModel getViewModel() {
        return mViewModel;
    }
}
