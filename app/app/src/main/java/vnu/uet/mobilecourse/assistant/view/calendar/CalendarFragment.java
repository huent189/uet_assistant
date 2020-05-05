package vnu.uet.mobilecourse.assistant.view.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import vnu.uet.mobilecourse.assistant.R;
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

        calendarView.setOnDateClickListener(new CustomCalendarView.OnDateClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, Date date, long id) {
                updateDate(tvDate, date);
            }
        });

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
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String selectedDate = sdf.format(date);
        selectedDate = "Ngày " + selectedDate;
        tvDate.setText(selectedDate);
    }
}
