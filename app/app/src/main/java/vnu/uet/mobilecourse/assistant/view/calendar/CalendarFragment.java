package vnu.uet.mobilecourse.assistant.view.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.DailyEventAdapter;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.model.todo.DailyTodoList;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.view.component.SwipeToDeleteCallback;
import vnu.uet.mobilecourse.assistant.viewmodel.CalendarViewModel;

public class CalendarFragment extends Fragment {

    private CalendarViewModel calendarViewModel;

    private CustomCalendarView calendarView;

    private DailyEventAdapter dailyEventAdapter;

    private NavController navController;

    private RecyclerView rvDailyTodoList;

    private FragmentActivity activity;

    private Toolbar toolbar;

    private CollapsingToolbarLayout collapsingToolbarLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        activity = getActivity();

        calendarViewModel = new ViewModelProvider(activity).get(CalendarViewModel.class);

        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        initializeToolbar(root);

        collapsingToolbarLayout = root.findViewById(R.id.col);

        if (activity != null)
            navController = Navigation.findNavController(activity, R.id.nav_host_fragment);

        rvDailyTodoList = root.findViewById(R.id.rvDailyTodoList);

        rvDailyTodoList.setLayoutManager(new LinearLayoutManager(this.getContext()));

//        tvDate = root.findViewById(R.id.tvDate);

        calendarView = root.findViewById(R.id.calendar_view);

        calendarView.setOnDateChangeListener(this::updateDate);

        FloatingActionButton fabAddTodo = root.findViewById(R.id.fabAddTodo);
        fabAddTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTodo();
            }
        });

        enableSwipeToDeleteAndUndo();

        return root;
    }

    private void initializeToolbar(View root) {
        if (activity instanceof AppCompatActivity) {
            toolbar = root.findViewById(R.id.toolbar);

            ((AppCompatActivity) activity).setSupportActionBar(toolbar);
            ((AppCompatActivity) activity).getSupportActionBar().setDisplayShowTitleEnabled(true);

            setHasOptionsMenu(true);
        }
    }

    private void updateDate(Date date) {
        String selectedDate = DateTimeUtils.DATE_FORMAT.format(date);

        if (activity instanceof AppCompatActivity && toolbar != null) {
            collapsingToolbarLayout.setTitle(selectedDate);
        }

//        CalendarFragment fragment = this;

        calendarViewModel.getDailyTodoList(date).observe(getViewLifecycleOwner(), stateModel -> {
            switch (stateModel.getStatus()) {
                case SUCCESS:
                    DailyTodoList dailyTodoList = stateModel.getData();

                    dailyEventAdapter = new DailyEventAdapter(dailyTodoList, CalendarFragment.this);
                    rvDailyTodoList.setAdapter(dailyEventAdapter);

                    calendarView.notifyTodoSetChanged(getViewLifecycleOwner());

                    break;
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.calendar_toolbar_menu, menu);
    }

    private void addTodo() {
        Date date = calendarView.getSelectedDate();
        String currentDate = DateTimeUtils.SHORT_DATE_FORMAT.format(date);
        Bundle bundle = new Bundle();
        bundle.putString("currentDate", currentDate);

        navController.navigate(R.id.action_navigation_calendar_to_addTodoFragment, bundle);
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final Todo item = dailyEventAdapter.getTodoList().get(position);

                calendarViewModel.deleteTodo(item.getId());
//                dailyEventAdapter.getTodoList(position);


//                Snackbar snackbar = Snackbar
//                        .make(coordinatorLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
//                snackbar.setAction("UNDO", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        mAdapter.restoreItem(item, position);
//                        recyclerView.scrollToPosition(position);
//                    }
//                });
//
//                snackbar.setActionTextColor(Color.YELLOW);
//                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(rvDailyTodoList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.action_view_todo_list) {
            navController.navigate(R.id.action_navigation_calendar_to_navigation_todo_lists);
        }

        return super.onOptionsItemSelected(item);
    }

    public CalendarViewModel getViewModel() {
        return calendarViewModel;
    }
}
