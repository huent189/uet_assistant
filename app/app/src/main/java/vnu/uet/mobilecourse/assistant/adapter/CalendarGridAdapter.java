package vnu.uet.mobilecourse.assistant.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import vnu.uet.mobilecourse.assistant.R;

public class CalendarGridAdapter extends ArrayAdapter {

    private List<Date> dates;

    private Calendar currentCalendar;

    private LayoutInflater inflater;

    public CalendarGridAdapter(@NonNull Context context, List<Date> dates, Calendar currentCalendar) {
        super(context, R.layout.layout_calendar_cell);

        this.currentCalendar = currentCalendar;
        this.dates = dates;

        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Date dateOfMonth = dates.get(position);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(dateOfMonth);

        int dayOfMonth = dateCalendar.get(Calendar.DAY_OF_MONTH);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_calendar_cell, parent, false);

        }

        if (!isSameMonthAndYear(currentCalendar, dateCalendar)) {
            //TODO: disable cell
        } else if (currentCalendar.getTime().equals(dateOfMonth)) {
            ImageView ivSelectedCircle = convertView.findViewById(R.id.ivSelectedCircle);
            ivSelectedCircle.setVisibility(View.VISIBLE);
        }

        TextView tvDayOfMonth = convertView.findViewById(R.id.tvDayOfMonth);
        tvDayOfMonth.setText(String.valueOf(dayOfMonth));

        return convertView;
    }

    private boolean isSameMonthAndYear(Calendar current, Calendar target) {
        int targetMonth = target.get(Calendar.MONTH) + 1;
        int currentMonth = current.get(Calendar.MONTH) + 1;
        int targetYear = target.get(Calendar.YEAR);
        int currentYear = current.get(Calendar.YEAR);

        return targetMonth == currentMonth && targetYear == currentYear;
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return dates.indexOf(item);
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }
}
