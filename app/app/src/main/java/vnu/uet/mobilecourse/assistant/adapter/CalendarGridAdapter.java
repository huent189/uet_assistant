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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.todo.DailyTodoList;
import vnu.uet.mobilecourse.assistant.repository.TodoRepository;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class CalendarGridAdapter extends ArrayAdapter {

    private List<Date> dates;

    private Calendar currentCalendar;

    private LayoutInflater inflater;

    private Date defaultSelectedDate;

    private boolean isShowTodo;

    private LifecycleOwner lifecycleOwner;

    public CalendarGridAdapter(@NonNull Context context, List<Date> dates, Calendar currentCalendar,
                               Date defaultSelectedDate, boolean isShowTodo) {
        super(context, R.layout.layout_calendar_cell);

        this.currentCalendar = currentCalendar;
        this.defaultSelectedDate = defaultSelectedDate;
        this.dates = dates;
        this.isShowTodo = isShowTodo;

        inflater = LayoutInflater.from(context);
    }

    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
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

        Context context = getContext();

        if (!DateTimeUtils.isSameMonthAndYear(currentCalendar, dateCalendar)) {
            // disable cell
            int color = ContextCompat.getColor(context, R.color.whiteDisable);
            tvDayOfMonth.setTextColor(color);

        }

        if (DateTimeUtils.isSameDate(defaultSelectedDate, dateOfMonth)) {
            ImageView ivSelectedCircle = convertView.findViewById(R.id.ivSelectedCircle);
            ivSelectedCircle.setVisibility(View.VISIBLE);
        } else if (DateTimeUtils.isSameDate(currentDate, dateOfMonth)) {
            int color = ContextCompat.getColor(context, R.color.primary);
            tvDayOfMonth.setTextColor(color);
        }

        ImageView ivHaveTodo = convertView.findViewById(R.id.ivHaveTodo);

        if (isShowTodo) {
            StateMediatorLiveData<DailyTodoList> dailyList = TodoRepository.getInstance().getDailyTodoList(dateOfMonth);

            if (lifecycleOwner != null) {
                dailyList.observe(lifecycleOwner, new Observer<StateModel<DailyTodoList>>() {
                    @Override
                    public void onChanged(StateModel<DailyTodoList> stateModel) {
                        if (stateModel.getStatus() == StateStatus.SUCCESS) {
                            DailyTodoList dailyTodoList = stateModel.getData();

                            if (dailyTodoList != null && !dailyTodoList.isEmpty()) {
                                ivHaveTodo.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }

        } else {
            ivHaveTodo.setVisibility(View.GONE);
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
