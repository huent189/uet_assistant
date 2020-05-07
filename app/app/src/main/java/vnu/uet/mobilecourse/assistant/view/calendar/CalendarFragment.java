package vnu.uet.mobilecourse.assistant.view.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.AllCoursesAdapter;
import vnu.uet.mobilecourse.assistant.adapter.TodoAdapter;
import vnu.uet.mobilecourse.assistant.model.Course;
import vnu.uet.mobilecourse.assistant.model.DailyTodoList;
import vnu.uet.mobilecourse.assistant.model.Todo;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.viewmodel.CalendarViewModel;

public class CalendarFragment extends Fragment {

    private CalendarViewModel calendarViewModel;

    private CustomCalendarView calendarView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        TextView tvDate = root.findViewById(R.id.tvDate);

        calendarView = root.findViewById(R.id.calendar_view);

        calendarView.setOnDateChangeListener(new CustomCalendarView.OnDateChangeListener() {
            @Override
            public void onDateChange(Date date) {
                updateDate(tvDate, date);
            }
        });

        DailyTodoList todoList = new DailyTodoList(Calendar.getInstance().getTime());

        Todo todo = new Todo();
        todo.setTitle("Helo sf 1");
        todo.setDeadline(Calendar.getInstance().getTime());

        todoList.offer(todo);

        Todo todo2 = new Todo();
        todo2.setTitle("Helo sf2");
        todo2.setDeadline(Calendar.getInstance().getTime());
        todoList.offer(todo2);

        TodoAdapter todoAdapter = new TodoAdapter(todoList, this);

        RecyclerView rvDailyTodoList = root.findViewById(R.id.rvDailyTodoList);
        rvDailyTodoList.setAdapter(todoAdapter);
        rvDailyTodoList.setLayoutManager(new LinearLayoutManager(this.getContext()));
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

    private void updateDate(TextView tvDate, Date date) {
        String selectedDate = DateTimeUtils.DATE_FORMAT.format(date);
        tvDate.setText(selectedDate);
    }
}
