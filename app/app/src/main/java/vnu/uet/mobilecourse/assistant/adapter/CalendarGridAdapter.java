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
import androidx.core.content.ContextCompat;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;

public class CalendarGridAdapter extends ArrayAdapter {

    private List<Date> dates;

    private Calendar currentCalendar;

    private LayoutInflater inflater;

    private int defaultSelectedId;

    public CalendarGridAdapter(@NonNull Context context, List<Date> dates, Calendar currentCalendar, int defaultSelectedId) {
        super(context, R.layout.layout_calendar_cell);

        this.currentCalendar = currentCalendar;
        this.defaultSelectedId = defaultSelectedId;
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

        if (position == defaultSelectedId) {
            ImageView ivSelectedCircle = convertView.findViewById(R.id.ivSelectedCircle);
            ivSelectedCircle.setVisibility(View.VISIBLE);
        }

        if (!DateTimeUtils.isSameMonthAndYear(currentCalendar, dateCalendar)) {
            //TODO: disable cell
            Context context = getContext();
            int color = ContextCompat.getColor(context, R.color.whiteDisable);
            tvDayOfMonth.setTextColor(color);

        } else if (DateTimeUtils.isSameDate(currentDate, dateOfMonth)) {
            ImageView ivCurrentDate = convertView.findViewById(R.id.ivCurrentDate);
            ivCurrentDate.setVisibility(View.VISIBLE);
        }

        return convertView;
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
