package vnu.uet.mobilecourse.assistant.view.calendar;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import vnu.uet.mobilecourse.assistant.adapter.CalendarGridAdapter;

public class CustomCalendarView extends BasicCalendarView {

    public CustomCalendarView(Context context) {
        super(context);
    }

    public CustomCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void prepareLayout() {
        isShowTodo = true;
    }

    public void notifyTodoSetChanged(LifecycleOwner lifecycleOwner) {
        gridAdapter = new CalendarGridAdapter(context, dates, calendar, selectedDate, isShowTodo);
        gridAdapter.setLifecycleOwner(lifecycleOwner);
        gvDates.setAdapter(gridAdapter);
    }
}