package vnu.uet.mobilecourse.assistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.todo.DailyTodoList;
import vnu.uet.mobilecourse.assistant.repository.TodoRepository;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;

public class CalendarGridAdapter extends ArrayAdapter {

    private List<Date> dates;

    private Calendar currentCalendar;

    private LayoutInflater inflater;

    private Date defaultSelectedDate;

    public CalendarGridAdapter(@NonNull Context context, List<Date> dates, Calendar currentCalendar, Date defaultSelectedDate) {
        super(context, R.layout.layout_calendar_cell);

        this.currentCalendar = currentCalendar;
        this.defaultSelectedDate = defaultSelectedDate;
        this.dates = dates;

        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Date dateOfMonth = dates.get(position);

        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(dateOfMonth);

        Date currentDate = Calendar.getInstance().getTime();

        int dayOfMonth = dateCalendar.get(Calendar.DAY_OF_MONTH);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_calendar_cell, parent, false);
        }

        TextView tvDayOfMonth = convertView.findViewById(R.id.tvDayOfMonth);
        tvDayOfMonth.setText(String.valueOf(dayOfMonth));

        if (!DateTimeUtils.isSameMonthAndYear(currentCalendar, dateCalendar)) {
            //TODO: disable cell
            Context context = getContext();
            int color = ContextCompat.getColor(context, R.color.whiteDisable);
            tvDayOfMonth.setTextColor(color);

        } else if (DateTimeUtils.isSameDate(currentDate, dateOfMonth)) {
            ImageView ivCurrentDate = convertView.findViewById(R.id.ivCurrentDate);
            ivCurrentDate.setVisibility(View.VISIBLE);

        } else if (DateTimeUtils.isSameDate(defaultSelectedDate, dateOfMonth)) {
            ImageView ivSelectedCircle = convertView.findViewById(R.id.ivSelectedCircle);
            ivSelectedCircle.setVisibility(View.VISIBLE);

        }

        String dateString = DateTimeUtils.DATE_FORMAT.format(dateOfMonth);
        Map<String, DailyTodoList> dailyLists = TodoRepository.getInstance().getDailyLists();
        if (dailyLists.containsKey(dateString)) {
            DailyTodoList currentDailyList = dailyLists.get(dateString);

            if (currentDailyList != null && !currentDailyList.isEmpty()) {
                ImageView ivHaveTodo = convertView.findViewById(R.id.ivHaveTodo);
                ivHaveTodo.setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public int getPosition(@Nullable Object item) {
        if (item instanceof Date)
            return dates.indexOf(item);

        return -1;
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }
}
