package vnu.uet.mobilecourse.assistant.view.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
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

public class BasicCalendarView extends LinearLayout {

    protected TextView tvCurrentMonth;

    protected CalendarDatesGridView gvDates;

    protected static final int MAX_CALENDAR_DAYS_BOUNDARY = 42;
    protected static final int MIN_CALENDAR_DAYS_BOUNDARY = 35;

    protected Calendar calendar = Calendar.getInstance();

    protected Date selectedDate = calendar.getTime();

    protected Context context;

    protected List<Date> dates = new ArrayList<>();

    protected CustomCalendarView.OnDateChangeListener onDateChangeListener;

    protected CalendarGridAdapter gridAdapter;

    protected boolean isShowTodo;

    public BasicCalendarView(Context context) {
        super(context);

        this.context = context;

        initializeLayout();
    }

    protected void prepareLayout() {

    }

    public BasicCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        initializeLayout();
    }

    public BasicCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.context = context;

        initializeLayout();
    }

    protected final void navigateToNextMonth() {
        calendar.add(Calendar.MONTH, 1);
        setupCalendar();
    }

    protected final void navigateToPrevMonth() {
        calendar.add(Calendar.MONTH, -1);
        setupCalendar();
    }

    public final void navigateToNextDay() {
        int index = dates.indexOf(selectedDate);
        updateSelectedDate(index + 1);
        setupCalendar();
        onDateChangeListener.onDateChange(selectedDate);
    }

    public final void navigateToPrevDay() {
        int index = dates.indexOf(selectedDate);
        updateSelectedDate(index - 1);
        setupCalendar();
        onDateChangeListener.onDateChange(selectedDate);
    }

    public final void setOnDateChangeListener(CustomCalendarView.OnDateChangeListener listener) {
        onDateChangeListener = listener;

        gvDates.setOnItemClickListener((parent, view, position, id) -> {
            updateSelectedDate(position);
        });

        onDateChangeListener.onDateChange(selectedDate);
    }

    private void updateSelectedDate(int position) {
        Date date = dates.get(position);

//        int prevSelectedIndex = dates.indexOf(selectedDate);
//        View unselectedItem = gvDates.getChildAt(prevSelectedIndex);
//        updateItemView(unselectedItem, ItemState.UNSELECTED);
//
//        View selectedItem = gvDates.getChildAt(position);
//        updateItemView(selectedItem, ItemState.SELECTED);

        selectedDate = date;

        onDateChangeListener.onDateChange(date);

        if (!DateTimeUtils.isSameMonthAndYear(date, calendar.getTime())) {
            int delta = date.compareTo(calendar.getTime());

            if (delta < 0) {
                navigateToPrevMonth();
            } else
                navigateToNextMonth();
        }
    }

//    @Deprecated
//    private void updateItemView(View itemView, ItemState state) {
//        ImageView ivSelectedCircle = itemView.findViewById(R.id.ivSelectedCircle);
//
//        switch (state) {
//            case SELECTED:
//                ivSelectedCircle.setVisibility(View.VISIBLE);
//                break;
//
//            case UNSELECTED:
//                ivSelectedCircle.setVisibility(View.INVISIBLE);
//                break;
//        }
//    }

    private void initializeLayout() {
        prepareLayout();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            View view = inflater.inflate(R.layout.layout_calendar, this);

            tvCurrentMonth = view.findViewById(R.id.tvCurrentMonth);

            gvDates = view.findViewById(R.id.gvDates);

            ImageButton btnNext = view.findViewById(R.id.ibtnNext);
            ImageButton btnPrev = view.findViewById(R.id.ibtnPrevious);

            btnNext.setOnClickListener(v -> {
                // calculate next selected date
                Calendar nextMonthCalendar = (Calendar) calendar.clone();
                nextMonthCalendar.setTime(selectedDate);
                nextMonthCalendar.add(Calendar.MONTH, 1);
                selectedDate = nextMonthCalendar.getTime();

                navigateToNextMonth();
            });

            btnPrev.setOnClickListener(v -> {
                // calculate previous selected date
                Calendar nextMonthCalendar = (Calendar) calendar.clone();
                nextMonthCalendar.setTime(selectedDate);
                nextMonthCalendar.add(Calendar.MONTH, -1);
                selectedDate = nextMonthCalendar.getTime();

                navigateToPrevMonth();
            });

//            setOnTouchListener(new OnSwipeTouchListener(getContext()) {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    return gestureDetector.onTouchEvent(event);
//                }
//
//                @Override
//                public void onSwipeLeft() {
//                    // calculate next selected date
//                    Calendar nextMonthCalendar = (Calendar) calendar.clone();
//                    nextMonthCalendar.setTime(selectedDate);
//                    nextMonthCalendar.add(Calendar.MONTH, 1);
//                    selectedDate = nextMonthCalendar.getTime();
//
//                    navigateToNextMonth();
//                }
//
//                @Override
//                public void onSwipeRight() {
//                    // calculate previous selected date
//                    Calendar nextMonthCalendar = (Calendar) calendar.clone();
//                    nextMonthCalendar.setTime(selectedDate);
//                    nextMonthCalendar.add(Calendar.MONTH, -1);
//                    selectedDate = nextMonthCalendar.getTime();
//
//                    navigateToPrevMonth();
//                }
//            });
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
        gridAdapter = new CalendarGridAdapter(context, dates, calendar, selectedDate, isShowTodo);
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
    protected enum ItemState {
        SELECTED,
        UNSELECTED
    }

    public final Date getSelectedDate() {
        return selectedDate;
    }
}
