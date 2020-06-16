package vnu.uet.mobilecourse.assistant.view.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
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

    protected TextView mTvCurrentMonth;
    protected CalendarDatesGridView mGvDates;

    protected static final int MAX_CALENDAR_DAYS_BOUNDARY = 42;
    protected static final int MIN_CALENDAR_DAYS_BOUNDARY = 35;

    protected Calendar mCalendar = Calendar.getInstance();
    protected Date mSelectedDate = mCalendar.getTime();
    protected List<Date> mDates = new ArrayList<>();

    protected Context mContext;

    protected CustomCalendarView.OnDateChangeListener mOnDateChangeListener;

    protected CalendarGridAdapter mGridAdapter;

    protected boolean mShowTodo;

    public BasicCalendarView(Context context) {
        super(context);

        this.mContext = context;

        initializeLayout();
    }

    protected void prepareLayout() {

    }

    public BasicCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.mContext = context;

        initializeLayout();
    }

    public BasicCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mContext = context;

        initializeLayout();
    }

    protected final void navigateToNextMonth() {
        mCalendar.add(Calendar.MONTH, 1);
        setupCalendar();
    }

    protected final void navigateToPrevMonth() {
        mCalendar.add(Calendar.MONTH, -1);
        setupCalendar();
    }

    public final void setOnDateChangeListener(CustomCalendarView.OnDateChangeListener listener) {
        mOnDateChangeListener = listener;

        mGvDates.setOnItemClickListener((parent, view, position, id) -> updateSelectedDate(position));

        mOnDateChangeListener.onDateChange(mSelectedDate);
    }

    private void updateSelectedDate(int position) {
        Date date = mDates.get(position);

        mSelectedDate = date;

        mOnDateChangeListener.onDateChange(date);

        if (!DateTimeUtils.isSameMonthAndYear(date, mCalendar.getTime())) {
            int delta = date.compareTo(mCalendar.getTime());

            if (delta < 0) {
                navigateToPrevMonth();
            } else
                navigateToNextMonth();
        }
    }

    private void initializeLayout() {
        prepareLayout();

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            View view = inflater.inflate(R.layout.layout_calendar, this);

            mTvCurrentMonth = view.findViewById(R.id.tvCurrentMonth);

            mGvDates = view.findViewById(R.id.gvDates);

            ImageButton btnNext = view.findViewById(R.id.ibtnNext);
            ImageButton btnPrev = view.findViewById(R.id.ibtnPrevious);

            btnNext.setOnClickListener(v -> {
                // calculate next selected date
                Calendar nextMonthCalendar = (Calendar) mCalendar.clone();
                nextMonthCalendar.setTime(mSelectedDate);
                nextMonthCalendar.add(Calendar.MONTH, 1);
                mSelectedDate = nextMonthCalendar.getTime();

                navigateToNextMonth();
            });

            btnPrev.setOnClickListener(v -> {
                // calculate previous selected date
                Calendar nextMonthCalendar = (Calendar) mCalendar.clone();
                nextMonthCalendar.setTime(mSelectedDate);
                nextMonthCalendar.add(Calendar.MONTH, -1);
                mSelectedDate = nextMonthCalendar.getTime();

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
        Date currentDate = mCalendar.getTime();
        String currentMonth = DateTimeUtils.MONTH_FORMAT.format(currentDate);
        mTvCurrentMonth.setText(currentMonth);

        // setup calendar with selected month
        Calendar monthCalendar = (Calendar) mCalendar.clone();

        // find first date display in calendar
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth);

        // clear all prev dates
        mDates.clear();

        // append date into date list
        while (mDates.size() < MAX_CALENDAR_DAYS_BOUNDARY) {
            mDates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // remove unnecessary date in calendar
        Date boundaryDate = mDates.get(MIN_CALENDAR_DAYS_BOUNDARY);
        if (!DateTimeUtils.isSameMonthAndYear(currentDate, boundaryDate)) {
            for (int i = 0; i < 7; i++) {
                mDates.remove(MIN_CALENDAR_DAYS_BOUNDARY);
            }
        }

        // create adapter
        mGridAdapter = new CalendarGridAdapter(mContext, mDates, mCalendar, mSelectedDate, mShowTodo);
        mGvDates.setAdapter(mGridAdapter);

        // in case change between month in calendar
        // re-render daily TodoList
        if (mOnDateChangeListener != null) {
            mOnDateChangeListener.onDateChange(mSelectedDate);
        }
    }

    /**
     * On date change listener on calendar
     */
    public interface OnDateChangeListener {
        void onDateChange(Date date);
    }

    public final Date getSelectedDate() {
        return mSelectedDate;
    }
}
