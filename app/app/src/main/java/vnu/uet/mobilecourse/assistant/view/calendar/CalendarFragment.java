package vnu.uet.mobilecourse.assistant.view.calendar;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.TodoAdapter;
import vnu.uet.mobilecourse.assistant.model.todo.DailyTodoList;
import vnu.uet.mobilecourse.assistant.model.todo.Todo;
import vnu.uet.mobilecourse.assistant.repository.TodoRepository;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.viewmodel.CalendarViewModel;

public class CalendarFragment extends Fragment {

    private CalendarViewModel calendarViewModel;

    private CustomCalendarView calendarView;

    private TextView tvDate;

    private TodoAdapter todoAdapter;

    private NavController navController;

    private RecyclerView rvDailyTodoList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        initializeToolbar(root);

        Activity activity = getActivity();

        if (activity != null)
            navController = Navigation.findNavController(activity, R.id.nav_host_fragment);

        rvDailyTodoList = root.findViewById(R.id.rvDailyTodoList);

        rvDailyTodoList.setLayoutManager(new LinearLayoutManager(this.getContext()));

        tvDate = root.findViewById(R.id.tvDate);

        calendarView = root.findViewById(R.id.calendar_view);

        calendarView.setOnDateChangeListener(this::updateDate);

        return root;
    }

    private void initializeToolbar(View root) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        if (activity != null) {
            Toolbar toolbar = root.findViewById(R.id.toolbar);

            activity.setSupportActionBar(toolbar);

            setHasOptionsMenu(true);
        }
    }

    private void updateDate(Date date) {
        String selectedDate = DateTimeUtils.DATE_FORMAT.format(date);
        tvDate.setText(selectedDate);

        LiveData<DailyTodoList> dailyList = TodoRepository.getInstance().getTodoListByDate(date);

        CalendarFragment fragment = this;

        dailyList.observe(getViewLifecycleOwner(), dailyTodoList -> {
            todoAdapter = new TodoAdapter(dailyTodoList, fragment);
            rvDailyTodoList.setAdapter(todoAdapter);


            calendarView.notifyTodoSetChanged(getViewLifecycleOwner());
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.todo_toolbar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_create:
                Date date = calendarView.getSelectedDate();
                String currentDate = DateTimeUtils.SHORT_DATE_FORMAT.format(date);
                Bundle bundle = new Bundle();
                bundle.putString("currentDate", currentDate);

                navController.navigate(R.id.action_navigation_calendar_to_addTodoFragment, bundle);
        }

        return super.onOptionsItemSelected(item);
    }
}
