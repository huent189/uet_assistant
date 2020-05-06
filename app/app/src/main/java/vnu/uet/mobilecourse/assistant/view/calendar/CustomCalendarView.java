package vnu.uet.mobilecourse.assistant.view.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.CalendarGridAdapter;
import vnu.uet.mobilecourse.assistant.model.Todo;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;

public class CustomCalendarView extends LinearLayout {

    private ImageButton ibtnPrevious, ibtnNext;

    private TextView tvCurrentMonth;

    private CalendarDatesGridView gvDates;

    private static final int MAX_CALENDAR_DAYS = 42;

    private Calendar calendar = Calendar.getInstance();

    private Context context;

    private List<Date> dates = new ArrayList<>();

    private List<Todo> todos = new ArrayList<>();

    private int prevSelectedIndex = -1;

    private OnDateChangeListener onDateChangeListener;

    public CustomCalendarView(Context context) {
        super(context);
    }

    public CustomCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        initializeLayout();

        ibtnPrevious.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToNextMonth();
            }
        });

        ibtnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToPrevMonth();
            }
        });
    }

    private void navigateToNextMonth() {
        calendar.add(Calendar.MONTH, 1);
        setupCalendar();
    }

    private void navigateToPrevMonth() {
        calendar.add(Calendar.MONTH, -1);
        setupCalendar();
    }

    public void setOnDateChangeListener(OnDateChangeListener listener) {
        onDateChangeListener = listener;

        gvDates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateSelectedDate(position);
            }
        });

        Date currentDate = calendar.getTime();
        prevSelectedIndex = dates.indexOf(currentDate);
        onDateChangeListener.onDateChange(currentDate);
    }

    private void updateSelectedDate(int position) {
        Date date = dates.get(position);

        if (prevSelectedIndex > 0) {
            View unselectedItem = gvDates.getChildAt(prevSelectedIndex);
            updateItemView(unselectedItem, ItemState.UNSELECTED);
        }

        View selectedItem = gvDates.getChildAt(position);
        updateItemView(selectedItem, ItemState.SELECTED);

        onDateChangeListener.onDateChange(date);

        prevSelectedIndex = position;

        if (!DateTimeUtils.isSameMonthAndYear(date, calendar.getTime())) {
            int delta = date.compareTo(calendar.getTime());

            if (delta < 0) {
                navigateToPrevMonth();
            } else
                navigateToNextMonth();
        }
    }

    private void updateItemView(View itemView, ItemState state) {
        ImageView ivSelectedCircle = itemView.findViewById(R.id.ivSelectedCircle);

        switch (state) {
            case SELECTED:
                ivSelectedCircle.setVisibility(View.VISIBLE);
                break;

            case UNSELECTED:
                ivSelectedCircle.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public CustomCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.context = context;
    }

    private void initializeLayout() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            View view = inflater.inflate(R.layout.layout_calendar, this);

            ibtnNext = view.findViewById(R.id.ibtnNext);
            ibtnPrevious = view.findViewById(R.id.ibtnPrevious);
            tvCurrentMonth = view.findViewById(R.id.tvCurrentMonth);
            gvDates = view.findViewById(R.id.gvDates);

            gvDates.setOnSwipeListener(new CalendarDatesGridView.OnSwipeListener() {
                @Override
                public void onSwipeLeft() {
                    navigateToNextMonth();
                }

                @Override
                public void onSwipeRight() {
                    navigateToPrevMonth();
                }
            });
        }

        setupCalendar();
    }

    private void setupCalendar() {
        // display selected month title
        String currentMonth = DateTimeUtils.MONTH_FORMAT.format(calendar.getTime());
        tvCurrentMonth.setText(currentMonth);

        // setup calendar with selected month
        Calendar monthCalendar = (Calendar) calendar.clone();

        // find first date display in calendar
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth);

        // clear all prev dates
        dates.clear();

        // append date into date list
        while (dates.size() < MAX_CALENDAR_DAYS) {
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // default selected date in calendar
        // especially needed in case change between month in calendar
        int defaultSelectedIdx = prevSelectedIndex;

        // create adapter
        CalendarGridAdapter gridAdapter = new CalendarGridAdapter(context, dates, calendar, defaultSelectedIdx);
        gvDates.setAdapter(gridAdapter);

        // in case change between month in calendar
        // re-render daily TodoList
        if (onDateChangeListener != null) {
            Date defaultSelectedDate = dates.get(defaultSelectedIdx);
            onDateChangeListener.onDateChange(defaultSelectedDate);
        }
    }

    /**
     * On date change listener on calendar
     */
    public interface OnDateChangeListener {
        void onDateChange(Date date);
    }

    /**
     * Calendar cell state: selected or unselected
     */
    private enum ItemState {
        SELECTED,
        UNSELECTED
    }
}
