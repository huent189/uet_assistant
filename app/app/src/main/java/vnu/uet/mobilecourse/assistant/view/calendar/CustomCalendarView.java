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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.CalendarGridAdapter;
import vnu.uet.mobilecourse.assistant.model.Todo;

public class CustomCalendarView extends LinearLayout {

    private ImageButton ibtnPrevious, ibtnNext;

    private TextView tvCurrentMonth;

    private CalendarDatesGridView gvDates;

    private static final int MAX_CALENDAR_DAYS = 35;

    private Calendar calendar = Calendar.getInstance();

    private Context context;

    private List<Date> dates = new ArrayList<>();

    private List<Todo> todos = new ArrayList<>();

    private int prevSelectedIndex = -1;

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static SimpleDateFormat monthFormat = new SimpleDateFormat("MM, yyyy");

    private CalendarGridAdapter gridAdapter;

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
                calendar.add(Calendar.MONTH, -1);
                setupCalendar();
            }
        });

        ibtnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, 1);
                setupCalendar();
            }
        });
    }

    public void setOnDateClickListener(OnDateClickListener listener) {
        gvDates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Date date = dates.get(position);

                if (prevSelectedIndex > 0) {
                    View unselectedItem = gvDates.getChildAt(prevSelectedIndex);
                    updateItemView(unselectedItem, ItemState.UNSELECTED);
                }

                View selectedItem = gvDates.getChildAt(position);
                updateItemView(selectedItem, ItemState.SELECTED);

                listener.onItemClick(parent, view, date, id);

                prevSelectedIndex = position;
            }
        });
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
        }

        setupCalendar();
    }

    private void setupCalendar() {
        String currentMonth = monthFormat.format(calendar.getTime());
        tvCurrentMonth.setText(currentMonth);

        Calendar monthCalendar = (Calendar) calendar.clone();

        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth);

        dates.clear();

        while (dates.size() < MAX_CALENDAR_DAYS) {
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        gridAdapter = new CalendarGridAdapter(context, dates, calendar);
        gvDates.setAdapter(gridAdapter);
    }

    public interface OnDateClickListener {
        void onItemClick(AdapterView<?> parent, View view, Date date, long id);
    }

    private enum ItemState {
        SELECTED,
        UNSELECTED
    }
}
