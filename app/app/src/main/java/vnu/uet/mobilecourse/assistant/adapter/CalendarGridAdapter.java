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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.event.DailyEventList;
import vnu.uet.mobilecourse.assistant.repository.firebase.EventRepository;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class CalendarGridAdapter extends ArrayAdapter<Date> {

    private List<Date> mDates;
    private Calendar mCalendar;
    private LayoutInflater mInflater;
    private Date mDefaultDate;
    private boolean mShowTodo;
    private LifecycleOwner mLifecycleOwner;

    public CalendarGridAdapter(@NonNull Context context, List<Date> dates, Calendar currentCalendar,
                               Date defaultSelectedDate, boolean isShowTodo) {

        super(context, R.layout.layout_calendar_cell);

        mCalendar = currentCalendar;
        mDefaultDate = defaultSelectedDate;
        mDates = dates;
        mShowTodo = isShowTodo;
        mInflater = LayoutInflater.from(context);
    }

    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        mLifecycleOwner = lifecycleOwner;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Date dateOfMonth = mDates.get(position);

        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(dateOfMonth);

        Date currentDate = Calendar.getInstance().getTime();

        int dayOfMonth = dateCalendar.get(Calendar.DAY_OF_MONTH);

        if (convertView == null) {
            convertView = mInflater
                    .inflate(R.layout.layout_calendar_cell, parent, false);
        }

        TextView tvDayOfMonth = convertView.findViewById(R.id.tvDayOfMonth);
        tvDayOfMonth.setText(String.valueOf(dayOfMonth));

        Context context = getContext();

        if (!DateTimeUtils.isSameMonthAndYear(mCalendar, dateCalendar)) {
            // disable cell
            int color = ContextCompat.getColor(context, R.color.whiteDisable);
            tvDayOfMonth.setTextColor(color);
        }

        // show primary circle to highlight selected date
        if (DateTimeUtils.isSameDate(mDefaultDate, dateOfMonth)) {
            ImageView ivSelectedCircle = convertView.findViewById(R.id.ivSelectedCircle);
            ivSelectedCircle.setVisibility(View.VISIBLE);

        }
        // set text color to primary if match current date
        else if (DateTimeUtils.isSameDate(currentDate, dateOfMonth)) {
            int color = ContextCompat.getColor(context, R.color.primary);
            tvDayOfMonth.setTextColor(color);
        }

        // notify if date have event
        ImageView ivHaveTodo = convertView.findViewById(R.id.ivHaveTodo);

        if (mShowTodo && mLifecycleOwner != null) {
            EventRepository.getInstance()
                    .getDailyEvent(dateOfMonth)
                    .observe(mLifecycleOwner, stateModel -> {
                        if (stateModel.getStatus() == StateStatus.SUCCESS) {
                            DailyEventList dailyTodoList = stateModel.getData();

                            if (dailyTodoList != null && !dailyTodoList.isEmpty()) {
                                ivHaveTodo.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        } else {
            ivHaveTodo.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return mDates.size();
    }

    @Override
    public int getPosition(@Nullable Date item) {
        return mDates.indexOf(item);
    }

    @Nullable
    @Override
    public Date getItem(int position) {
        return mDates.get(position);
    }
}
