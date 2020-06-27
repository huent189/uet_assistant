package vnu.uet.mobilecourse.assistant.view.calendar;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.DailyEventAdapter;
import vnu.uet.mobilecourse.assistant.alarm.scheduler.TodoScheduler;
import vnu.uet.mobilecourse.assistant.model.event.IEvent;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.model.event.DailyEventList;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.view.component.CustomCalendarView;
import vnu.uet.mobilecourse.assistant.view.component.SwipeToDeleteCallback;
import vnu.uet.mobilecourse.assistant.viewmodel.CalendarViewModel;

public class CalendarFragment extends Fragment {

    private CalendarViewModel mViewModel;

    private CustomCalendarView mCalendarView;
    private DailyEventAdapter mDailyEventAdapter;
    private RecyclerView mRvDailyTodoList;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private ShimmerFrameLayout mSflEvents;
    private View mLayoutEmpty;

    private NavController mNavController;
    private FragmentActivity mActivity;

    private Bundle mRecyclerViewState;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mActivity = getActivity();

        if (mActivity != null) {
            mViewModel = new ViewModelProvider(mActivity).get(CalendarViewModel.class);
            mNavController = Navigation.findNavController(mActivity, R.id.nav_host_fragment);
            mRecyclerViewState = mViewModel.getDailyListViewState();
        }

        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        // restore expandable toolbar state
        CoordinatorLayout coordinatorLayout = root.findViewById(R.id.coordinator_layout);
        ViewCompat.requestApplyInsets(coordinatorLayout);

        initializeToolbar(root);

        mCollapsingToolbar = root.findViewById(R.id.col);

        mSflEvents = root.findViewById(R.id.sflEvents);
        mSflEvents.startShimmerAnimation();

        mLayoutEmpty = root.findViewById(R.id.layoutEmpty);

        mRvDailyTodoList = root.findViewById(R.id.rvDailyTodoList);

        mRvDailyTodoList.setLayoutManager(new LinearLayoutManager(this.getContext()));

        restoreRecycleViewState();

        mCalendarView = root.findViewById(R.id.calendar_view);

        mCalendarView.setOnDateChangeListener(this::updateDate);

        FloatingActionButton fabAddTodo = root.findViewById(R.id.fabAddTodo);

        fabAddTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTodo();
            }
        });

        enableSwipeToDelete();

        return root;
    }

    private void initializeToolbar(View root) {
        if (mActivity instanceof AppCompatActivity) {
            mToolbar = root.findViewById(R.id.toolbar);
            ((AppCompatActivity) mActivity).setSupportActionBar(mToolbar);
            setHasOptionsMenu(true);
        }
    }

    private static final String KEY_RECYCLER_STATE = DailyEventAdapter.class.getName();

    public void saveRecycleViewState() {
        RecyclerView.LayoutManager layoutManager = mRvDailyTodoList.getLayoutManager();
        if (layoutManager != null) {
            mRecyclerViewState = new Bundle();
            Parcelable onSaveInstanceState = layoutManager.onSaveInstanceState();
            mRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, onSaveInstanceState);
        }
    }

    private void restoreRecycleViewState() {
        RecyclerView.LayoutManager layoutManager = mRvDailyTodoList.getLayoutManager();

        if (mRecyclerViewState != null && layoutManager != null) {
            Parcelable onSaveInstanceState = mRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            layoutManager.onRestoreInstanceState(onSaveInstanceState);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        saveRecycleViewState();
    }

    @Override
    public void onResume() {
        super.onResume();

        restoreRecycleViewState();
    }

    private void updateDate(Date date) {
        String selectedDate = DateTimeUtils.DATE_FORMAT.format(date);

        if (mActivity instanceof AppCompatActivity && mToolbar != null) {
            mCollapsingToolbar.setTitle(selectedDate);
        }

        mViewModel.getDailyTodoList(date).observe(getViewLifecycleOwner(), stateModel -> {
            switch (stateModel.getStatus()) {
                case LOADING:
                    mRvDailyTodoList.setVisibility(View.GONE);
                    mSflEvents.setVisibility(View.VISIBLE);
                    mLayoutEmpty.setVisibility(View.GONE);

                    break;

                case SUCCESS:
                    DailyEventList dailyTodoList = stateModel.getData();

                    mDailyEventAdapter = new DailyEventAdapter(dailyTodoList, CalendarFragment.this);
                    mRvDailyTodoList.setAdapter(mDailyEventAdapter);

                    mCalendarView.notifyTodoSetChanged(getViewLifecycleOwner());

                    restoreRecycleViewState();

                    if (dailyTodoList.isEmpty()) {
                        mLayoutEmpty.setVisibility(View.VISIBLE);
//                        mRvDailyTodoList.setVisibility(View.GONE);

                    } else {
                        mLayoutEmpty.setVisibility(View.GONE);
                    }

                    mRvDailyTodoList.setVisibility(View.VISIBLE);
                    mSflEvents.setVisibility(View.GONE);

                    break;
            }
        });
    }

    private void addTodo() {
        Date date = mCalendarView.getSelectedDate();
        String currentDate = DateTimeUtils.SHORT_DATE_FORMAT.format(date);
        Bundle bundle = new Bundle();
        bundle.putString("currentDate", currentDate);

        mNavController.navigate(R.id.action_navigation_calendar_to_addTodoFragment, bundle);
    }

    private void enableSwipeToDelete() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final IEvent item = mDailyEventAdapter.getTodoList().get(position);

                if (item instanceof Todo) {
                    Todo todo = (Todo) item;
                    mViewModel.deleteTodo(todo.getId());
                    TodoScheduler.getInstance(mActivity).cancel(todo);
                }
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(mRvDailyTodoList);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.calendar_toolbar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.action_view_todo_list) {
            mViewModel.setDailyListViewState(mRecyclerViewState);
            mNavController.navigate(R.id.action_navigation_calendar_to_navigation_todo_lists);
        }

        return super.onOptionsItemSelected(item);
    }

    public CalendarViewModel getViewModel() {
        return mViewModel;
    }
}
