package vnu.uet.mobilecourse.assistant.view.calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.TodoListAdapter;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.TodoViewHolder;
import vnu.uet.mobilecourse.assistant.alarm.scheduler.TodoScheduler;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.model.firebase.TodoList;
import vnu.uet.mobilecourse.assistant.view.component.EmptyView;
import vnu.uet.mobilecourse.assistant.view.component.SwipeToDeleteCallback;
import vnu.uet.mobilecourse.assistant.viewmodel.CalendarViewModel;
import vnu.uet.mobilecourse.assistant.viewmodel.expandable.ExpandableTodoList;
import vnu.uet.mobilecourse.assistant.work.remindHandler.TodoHandler;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class TodoListsFragment extends Fragment {

    private CalendarViewModel mViewModel;
    private FragmentActivity mActivity;
    private NavController mNavController;
    private TodoListAdapter mAdapter;
    private RecyclerView mRvTodoLists;
    private Bundle mRecyclerViewState;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mActivity = getActivity();

        if (mActivity != null) {
            mViewModel = new ViewModelProvider(mActivity).get(CalendarViewModel.class);
        }

        View root = inflater.inflate(R.layout.fragment_todo_lists, container, false);

        initializeToolbar(root);

        if (mActivity != null) {
            mNavController = Navigation
                    .findNavController(mActivity, R.id.nav_host_fragment);
        }

        mRvTodoLists = root.findViewById(R.id.rvTodoLists);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRvTodoLists.setLayoutManager(layoutManager);

//        TodoListsFragment thisFragment = this;

//        // get bundle from prev fragment
//        if (getArguments() == null) {
//            setArguments(new Bundle());
//        }
//
//        Bundle args = getArguments();

        mRecyclerViewState = mViewModel.getTodoListViewState();
//        restoreRecycleViewState();

        ShimmerFrameLayout shimmerRvTodoLists = root.findViewById(R.id.shimmerRvTodoLists);
        shimmerRvTodoLists.startShimmerAnimation();

        View emptyView = root.findViewById(R.id.layoutEmpty);

        mViewModel.getAllTodoLists().observe(getViewLifecycleOwner(), stateModel -> {
            switch (stateModel.getStatus()) {
                case SUCCESS:
                    shimmerRvTodoLists.setVisibility(View.GONE);

                    List<TodoList> todoLists = stateModel.getData();

                    if (todoLists.isEmpty()) {
                        mRvTodoLists.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        mRvTodoLists.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);

                        mAdapter = new TodoListAdapter(todoLists, TodoListsFragment.this);
                        mRvTodoLists.setAdapter(mAdapter);

                        restoreRecycleViewState();
                    }

                    break;

                case LOADING:
                    shimmerRvTodoLists.setVisibility(View.VISIBLE);
                    mRvTodoLists.setVisibility(View.GONE);
                    emptyView.setVisibility(View.GONE);
                    break;

                case ERROR:
                    break;
            }
        });

        enableSwipeToDelete();

        setupFloatingActionButton(root);

        return root;
    }

    private void setupFloatingActionButton(View root) {
        FloatingActionButton fabAddTodo = root.findViewById(R.id.fabAddTodo);
        fabAddTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNavController.navigate(R.id.action_navigation_todo_lists_to_navigation_add_todo);
            }
        });

        FloatingActionButton fabAddList = root.findViewById(R.id.fabAddList);
        fabAddList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNavController.navigate(R.id.action_navigation_todo_lists_to_navigation_add_todo_list);
            }
        });
    }

    private static final String KEY_RECYCLER_STATE = TodoListAdapter.class.getName();

    public void saveRecycleViewState() {
        mRecyclerViewState = new Bundle();
        Parcelable onSaveInstanceState = mRvTodoLists.getLayoutManager().onSaveInstanceState();
        mRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, onSaveInstanceState);
        mAdapter.onSaveInstanceState(mRecyclerViewState);
    }

    public void restoreRecycleViewState() {
        if (mRecyclerViewState != null) {
            mAdapter.onRestoreInstanceState(mRecyclerViewState);
            Parcelable onSaveInstanceState = mRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            mRvTodoLists.getLayoutManager().onRestoreInstanceState(onSaveInstanceState);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        saveRecycleViewState();
    }

    @Override
    public void onResume() {
        super.onResume();

        restoreRecycleViewState();
    }

    private void initializeToolbar(View root) {
        if (mActivity instanceof AppCompatActivity) {
            Toolbar toolbar = root.findViewById(R.id.toolbar);
            ((AppCompatActivity) mActivity).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);
        }
    }

    private void enableSwipeToDelete() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                if (viewHolder instanceof TodoViewHolder) {
                    final Todo item = ((TodoViewHolder) viewHolder).getTodo();
                    mViewModel.deleteTodo(item.getId());

                    TodoScheduler.getInstance(mActivity).cancel(item);
//                    TodoHandler.getInstance().cancel(mActivity, item);
                } else if (viewHolder instanceof TodoListAdapter.TodoListViewHolder) {
                    final ExpandableTodoList todoList = ((TodoListAdapter.TodoListViewHolder) viewHolder).getTodoList();
                    List<Todo> todos = todoList.getItems();

                    todos.forEach(todo -> TodoScheduler.getInstance(mActivity).cancel(todo));

                    mViewModel.deleteTodoList(todoList.getId(), todos);

//                    TodoHandler.getInstance().cancelByTodoList(mActivity, todoList.getId());
                }
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(mRvTodoLists);
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
            saveRecycleViewState();
            mViewModel.setTodoListViewState(mRecyclerViewState);
            mNavController.navigateUp();
        }

        return super.onOptionsItemSelected(item);
    }

    public CalendarViewModel getViewModel() {
        return mViewModel;
    }
}
