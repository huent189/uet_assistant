package vnu.uet.mobilecourse.assistant.view.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
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
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;

public class CustomCalendarView extends LinearLayout {

    private TextView tvCurrentMonth;

    private CalendarDatesGridView gvDates;

    private static final int MAX_CALENDAR_DAYS_BOUNDARY = 42;
    private static final int MIN_CALENDAR_DAYS_BOUNDARY = 35;

    private Calendar calendar = Calendar.getInstance();

    private Date selectedDate = calendar.getTime();

    private Context context;

    private List<Date> dates = new ArrayList<>();

//    private List<Todo> todos = new ArrayList<>();

    private OnDateChangeListener onDateChangeListener;

    public CustomCalendarView(Context context) {
        super(context);

        this.context = context;

        initializeLayout();
    }

    public CustomCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        initializeLayout();
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

        gvDates.setOnItemClickListener((parent, view, position, id) -> updateSelectedDate(position));

        onDateChangeListener.onDateChange(selectedDate);
    }

    private void updateSelectedDate(int position) {
        Date date = dates.get(position);

        int prevSelectedIndex = dates.indexOf(selectedDate);
        View unselectedItem = gvDates.getChildAt(prevSelectedIndex);
        updateItemView(unselectedItem, ItemState.UNSELECTED);

        View selectedItem = gvDates.getChildAt(position);
        updateItemView(selectedItem, ItemState.SELECTED);

        onDateChangeListener.onDateChange(date);

        selectedDate = date;

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

        initializeLayout();
    }

    private void initializeLayout() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            View view = inflater.inflate(R.layout.layout_calendar, this);

            tvCurrentMonth = view.findViewById(R.id.tvCurrentMonth);

            ImageButton ibtnNext = view.findViewById(R.id.ibtnNext);
            ibtnNext.setOnClickListener(v -> {
                // calculate next selected date
                Calendar nextMonthCalendar = (Calendar) calendar.clone();
                nextMonthCalendar.setTime(selectedDate);
                nextMonthCalendar.add(Calendar.MONTH, 1);
                selectedDate = nextMonthCalendar.getTime();

                navigateToNextMonth();
            });

            ImageButton ibtnPrevious = view.findViewById(R.id.ibtnPrevious);
            ibtnPrevious.setOnClickListener(v -> {
                // calculate previous selected date
                Calendar nextMonthCalendar = (Calendar) calendar.clone();
                nextMonthCalendar.setTime(selectedDate);
                nextMonthCalendar.add(Calendar.MONTH, -1);
                selectedDate = nextMonthCalendar.getTime();

                navigateToPrevMonth();
            });

            gvDates = view.findViewById(R.id.gvDates);
            gvDates.setOnSwipeListener(new CalendarDatesGridView.OnSwipeListener() {
                @Override
                public void onSwipeLeft() {
                    // calculate next selected date
                    Calendar nextMonthCalendar = (Calendar) calendar.clone();
                    nextMonthCalendar.setTime(selectedDate);
                    nextMonthCalendar.add(Calendar.MONTH, 1);
                    selectedDate = nextMonthCalendar.getTime();

                    navigateToNextMonth();
                }

                @Override
                public void onSwipeRight() {
                    // calculate previous selected date
                    Calendar nextMonthCalendar = (Calendar) calendar.clone();
                    nextMonthCalendar.setTime(selectedDate);
                    nextMonthCalendar.add(Calendar.MONTH, -1);
                    selectedDate = nextMonthCalendar.getTime();

                    navigateToPrevMonth();
                }
            });
        }

        setupCalendar();
    }

    private void setupCalendar() {
        // display selected month title
        Date currentDate = calendar.getTime();
        String currentMonth = DateTimeUtils.MONTH_FORMAT.format(currentDate);
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
        while (dates.size() < MAX_CALENDAR_DAYS_BOUNDARY) {
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // remove unnecessary date in calendar
        Date boundaryDate = dates.get(MIN_CALENDAR_DAYS_BOUNDARY);
        if (!DateTimeUtils.isSameMonthAndYear(currentDate, boundaryDate)) {
            for (int i = 0; i < 7; i++) {
                dates.remove(MIN_CALENDAR_DAYS_BOUNDARY);
            }
        }

        // create adapter
        CalendarGridAdapter gridAdapter = new CalendarGridAdapter(context, dates, calendar, selectedDate);
        gvDates.setAdapter(gridAdapter);

        // in case change between month in calendar
        // re-render daily TodoList
        if (onDateChangeListener != null) {
            onDateChangeListener.onDateChange(selectedDate);
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

    public Date getSelectedDate() {
        return selectedDate;
    }
}
