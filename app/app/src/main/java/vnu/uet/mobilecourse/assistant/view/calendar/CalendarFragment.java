package vnu.uet.mobilecourse.assistant.view.calendar;

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
import androidx.lifecycle.ViewModelProvider;

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

    private RecyclerView rvDailyTodoList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        //        todoAdapter = new TodoAdapter(todoList, this);

        initializeToolbar(root);

        rvDailyTodoList = root.findViewById(R.id.rvDailyTodoList);
//        rvDailyTodoList.setAdapter(todoAdapter);
        rvDailyTodoList.setLayoutManager(new LinearLayoutManager(this.getContext()));

        tvDate = root.findViewById(R.id.tvDate);

        calendarView = root.findViewById(R.id.calendar_view);

        calendarView.setOnDateChangeListener(new CustomCalendarView.OnDateChangeListener() {
            @Override
            public void onDateChange(Date date) {
                updateDate(date);
            }
        });

//        DailyTodoList todoList = new DailyTodoList(Calendar.getInstance().getTime());
//
//        Todo todo = new Todo();
//        todo.setTitle("Helo sf 1");
//        todo.setDeadline(Calendar.getInstance().getTime());
//
//        todoList.offer(todo);
//
//        Todo todo2 = new Todo();
//        todo2.setTitle("Helo sf2");
//        todo2.setDeadline(Calendar.getInstance().getTime());
//        todoList.offer(todo2);
//

//        initializeDate(tvDate, calendarView);
//
//        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
//                String selectedDate = String.format("Ngày %02d/%02d/%d", dayOfMonth, month, year);
//                tvDate.setText(selectedDate);
//            }
//        });
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

        Map<String, DailyTodoList> dailyLists = TodoRepository.getInstance().getDailyLists();
        DailyTodoList currentDailyList = dailyLists.get(selectedDate);

        if (currentDailyList == null) {
            currentDailyList = new DailyTodoList(date);
        }

        dailyLists.put(selectedDate, currentDailyList);

        todoAdapter = new TodoAdapter(currentDailyList, this);
        rvDailyTodoList.setAdapter(todoAdapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.todo_toolbar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Calendar calendar = Calendar.getInstance();

        int HOUR = calendar.get(Calendar.HOUR);
        int MINUTE = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            }
        }, HOUR, MINUTE, true);

        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Thoát", dialog);
        dialog.show();

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_create:
                Date currentDate = calendarView.getSelectedDate();
                String dateString = DateTimeUtils.DATE_FORMAT.format(currentDate);

                DailyTodoList dailyTodoList = TodoRepository.getInstance()
                        .getDailyLists()
                        .get(dateString);

                if (dailyTodoList == null)
                    dailyTodoList = new DailyTodoList(currentDate);

                Todo todo = new Todo();
                todo.setTitle("Todo " + new Random().nextInt(50));
                todo.setDeadline(currentDate);

                dailyTodoList.offer(todo);

                TodoRepository.getInstance().getDailyLists().put(dateString, dailyTodoList);

                todoAdapter = new TodoAdapter(dailyTodoList, this);
                rvDailyTodoList.setAdapter(todoAdapter);
        }

        return super.onOptionsItemSelected(item);
    }
}
